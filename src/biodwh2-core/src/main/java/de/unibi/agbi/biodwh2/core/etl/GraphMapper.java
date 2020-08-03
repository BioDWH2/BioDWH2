package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.schema.GraphQLSchemaWriter;
import de.unibi.agbi.biodwh2.core.schema.GraphSchema;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public final class GraphMapper extends Mapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMapper.class);
    private static final String MAPPED_TO_EDGE_LABEL = "MAPPED_TO";
    private static final String IDS_NODE_PROPERTY = "ids";

    public void map(final Workspace workspace, final List<DataSource> dataSources, final String inputGraphFilePath,
                    final String outputGraphFilePath) {
        copyGraph(inputGraphFilePath, outputGraphFilePath);
        final Graph graph = new Graph(outputGraphFilePath.replace(GraphMLGraphWriter.EXTENSION, Graph.EXTENSION), true);
        mapGraph(graph, dataSources);
        saveGraph(graph, outputGraphFilePath);
        saveGraphSchema(graph,
                        outputGraphFilePath.replace(GraphMLGraphWriter.EXTENSION, GraphQLSchemaWriter.EXTENSION));
        graph.dispose();
    }

    private void copyGraph(final String inputGraphFilePath, final String outputGraphFilePath) {
        final Path originalPath = Paths.get(inputGraphFilePath.replace(GraphMLGraphWriter.EXTENSION, Graph.EXTENSION));
        final Path copied = Paths.get(outputGraphFilePath.replace(GraphMLGraphWriter.EXTENSION, Graph.EXTENSION));
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to copy merged graph to mapped graph file", e);
        }
    }

    private void mapGraph(final Graph graph, final List<DataSource> dataSources) {
        final Map<String, MappingDescriber> dataSourceDescriberMap = new HashMap<>();
        for (final DataSource dataSource : dataSources)
            dataSourceDescriberMap.put(dataSource.getId(), dataSource.getMappingDescriber());
        mapNodes(graph, dataSourceDescriberMap);
        mapEdges(graph, dataSourceDescriberMap);
    }

    private void mapNodes(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap) {
        final Map<String, Set<Long>> idNodeIdMap = new HashMap<>();
        for (final Node node : graph.getNodes()) {
            final String dataSourceId = StringUtils.split(node.getLabel(), GraphExporter.LABEL_PREFIX_SEPARATOR)[0];
            final MappingDescriber dataSourceDescriber = dataSourceDescriberMap.get(dataSourceId);
            if (dataSourceDescriber != null) {
                final NodeMappingDescription mappingDescription = dataSourceDescriber.describe(graph, node);
                if (mappingDescription != null)
                    mergeMatchingNodes(graph, mappingDescription, idNodeIdMap, node.getId());
            }
        }
    }

    private void mergeMatchingNodes(final Graph graph, final NodeMappingDescription description,
                                    final Map<String, Set<Long>> idNodeIdMap, final long mappedNodeId) {
        final Set<Long> matchedNodeIds = matchNodesFromIds(idNodeIdMap, description);
        final Node mergedNode = mergeOrCreateMappingNode(graph, description, matchedNodeIds, idNodeIdMap);
        graph.addEdge(mappedNodeId, mergedNode.getId(), MAPPED_TO_EDGE_LABEL);
        for (final String id : (String[]) mergedNode.getProperty(IDS_NODE_PROPERTY)) {
            if (!idNodeIdMap.containsKey(id))
                idNodeIdMap.put(id, new HashSet<>());
            idNodeIdMap.get(id).add(mergedNode.getId());
        }
    }

    private Set<Long> matchNodesFromIds(final Map<String, Set<Long>> idNodeIdMap,
                                        final NodeMappingDescription description) {
        final Set<Long> matchedNodeIds = new HashSet<>();
        for (final String id : description.getIdentifiers())
            if (idNodeIdMap.containsKey(id))
                matchedNodeIds.addAll(idNodeIdMap.get(id));
        return matchedNodeIds;
    }

    private Node mergeOrCreateMappingNode(final Graph graph, final NodeMappingDescription description,
                                          final Set<Long> matchedNodeIds, final Map<String, Set<Long>> idNodeIdMap) {
        final Set<String> ids = new HashSet<>(description.getIdentifiers());
        Node mergedNode = null;
        for (final Long nodeId : matchedNodeIds) {
            final Node matchedNode = graph.getNode(nodeId);
            if (!hasMatchedNodeSameLabel(description, matchedNode))
                continue;
            final String[] nodeIds = matchedNode.getProperty(IDS_NODE_PROPERTY);
            Collections.addAll(ids, nodeIds);
            if (mergedNode == null)
                mergedNode = matchedNode;
            else {
                graph.mergeNodes(mergedNode, matchedNode);
                for (final String id : nodeIds)
                    idNodeIdMap.get(id).remove(nodeId);
            }
        }
        if (mergedNode == null)
            mergedNode = graph.addNode(description.type.toString());
        mergedNode.setProperty(IDS_NODE_PROPERTY, ids.toArray(new String[0]));
        graph.update(mergedNode);
        return mergedNode;
    }

    private boolean hasMatchedNodeSameLabel(final NodeMappingDescription description, final Node node) {
        return description.type.toString().equals(node.getLabel());
    }

    private void mapEdges(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap) {
        for (final Edge edge : graph.getEdges()) {
            final String dataSourceId = StringUtils.split(edge.getLabel(), GraphExporter.LABEL_PREFIX_SEPARATOR)[0];
            final MappingDescriber dataSourceDescriber = dataSourceDescriberMap.get(dataSourceId);
            if (dataSourceDescriber != null) {
                final EdgeMappingDescription mappingDescription = dataSourceDescriber.describe(graph, edge);
                if (mappingDescription != null) {
                    final Long[] mappedFromNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(edge.getFromId(),
                                                                                          MAPPED_TO_EDGE_LABEL);
                    final Long[] mappedToNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(edge.getToId(),
                                                                                        MAPPED_TO_EDGE_LABEL);
                    if (mappedFromNodeIds.length > 0 && mappedToNodeIds.length > 0) {
                        final Edge mappedEdge = graph.addEdge(mappedFromNodeIds[0], mappedToNodeIds[0],
                                                              mappingDescription.type.name());
                        mappedEdge.setProperty("source", dataSourceId);
                    }
                }
            }
        }
    }

    private void saveGraph(final Graph graph, final String outputGraphFilePath) {
        final GraphMLGraphWriter graphMLWriter = new GraphMLGraphWriter();
        graphMLWriter.write(outputGraphFilePath, graph);
    }

    private void saveGraphSchema(final Graph graph, final String filePath) {
        final GraphSchema schema = new GraphSchema(graph);
        try {
            new GraphQLSchemaWriter(schema).save(filePath);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to save graph schema file", e);
        }
    }
}
