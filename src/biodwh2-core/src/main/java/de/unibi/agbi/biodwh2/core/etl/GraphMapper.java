package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.schema.GraphQLSchemaWriter;
import de.unibi.agbi.biodwh2.core.schema.GraphSchema;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public final class GraphMapper extends Mapper {
    private static final String MappedToEdgeLabel = "MAPPED_TO";
    private static final String IdsNodeProperty = "ids";

    public void map(final Workspace workspace, final List<DataSource> dataSources, final String inputGraphFilePath,
                    final String outputGraphFilePath) {
        copyGraph(inputGraphFilePath, outputGraphFilePath);
        final Graph graph = new Graph(outputGraphFilePath.replace(GraphMLGraphWriter.Extension, Graph.Extension), true);
        mapGraph(graph, dataSources);
        saveGraph(graph, outputGraphFilePath);
        saveGraphSchema(graph,
                        outputGraphFilePath.replace(GraphMLGraphWriter.Extension, GraphQLSchemaWriter.Extension));
        graph.dispose();
    }

    private void copyGraph(final String inputGraphFilePath, final String outputGraphFilePath) {
        Path originalPath = Paths.get(inputGraphFilePath.replace(GraphMLGraphWriter.Extension, Graph.Extension));
        Path copied = Paths.get(outputGraphFilePath.replace(GraphMLGraphWriter.Extension, Graph.Extension));
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mapGraph(final Graph graph, final List<DataSource> dataSources) {
        final Map<String, MappingDescriber> dataSourceDescriberMap = new HashMap<>();
        for (DataSource dataSource : dataSources)
            dataSourceDescriberMap.put(dataSource.getId(), dataSource.getMappingDescriber());
        mapNodes(graph, dataSourceDescriberMap);
        mapEdges(graph, dataSourceDescriberMap);
    }

    private void mapNodes(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap) {
        final Map<String, Set<Long>> idNodeIdMap = new HashMap<>();
        for (Node node : graph.getNodes()) {
            String dataSourceId = StringUtils.split(node.getLabel(), GraphExporter.LabelPrefixSeparator)[0];
            MappingDescriber dataSourceDescriber = dataSourceDescriberMap.get(dataSourceId);
            if (dataSourceDescriber != null) {
                NodeMappingDescription mappingDescription = dataSourceDescriber.describe(graph, node);
                if (mappingDescription != null)
                    mergeMatchingNodes(graph, mappingDescription, idNodeIdMap, node.getId());
            }
        }
    }

    private void mergeMatchingNodes(final Graph graph, final NodeMappingDescription description,
                                    final Map<String, Set<Long>> idNodeIdMap, final long mappedNodeId) {
        Set<Long> matchedNodeIds = matchNodesFromIds(idNodeIdMap, description);
        Node mergedNode = mergeOrCreateMappingNode(graph, description, matchedNodeIds, idNodeIdMap);
        graph.addEdge(mappedNodeId, mergedNode.getId(), MappedToEdgeLabel);
        for (String id : (String[]) mergedNode.getProperty(IdsNodeProperty)) {
            if (!idNodeIdMap.containsKey(id))
                idNodeIdMap.put(id, new HashSet<>());
            idNodeIdMap.get(id).add(mergedNode.getId());
        }
    }

    private Set<Long> matchNodesFromIds(final Map<String, Set<Long>> idNodeIdMap,
                                        final NodeMappingDescription description) {
        final Set<Long> matchedNodeIds = new HashSet<>();
        for (String id : description.getIdentifiers())
            if (idNodeIdMap.containsKey(id))
                matchedNodeIds.addAll(idNodeIdMap.get(id));
        return matchedNodeIds;
    }

    private Node mergeOrCreateMappingNode(final Graph graph, final NodeMappingDescription description,
                                          final Set<Long> matchedNodeIds, final Map<String, Set<Long>> idNodeIdMap) {
        Set<String> ids = new HashSet<>(description.getIdentifiers());
        Node mergedNode = null;
        for (Long nodeId : matchedNodeIds) {
            Node matchedNode = graph.getNode(nodeId);
            if (!hasMatchedNodeSameLabel(description, matchedNode))
                continue;
            String[] nodeIds = matchedNode.getProperty(IdsNodeProperty);
            Collections.addAll(ids, nodeIds);
            if (mergedNode == null)
                mergedNode = matchedNode;
            else {
                graph.mergeNodes(mergedNode, matchedNode);
                for (String id : nodeIds)
                    idNodeIdMap.get(id).remove(nodeId);
            }
        }
        if (mergedNode == null)
            mergedNode = graph.addNode(description.type.toString());
        mergedNode.setProperty(IdsNodeProperty, ids.toArray(new String[0]));
        graph.update(mergedNode);
        return mergedNode;
    }

    private boolean hasMatchedNodeSameLabel(final NodeMappingDescription description, final Node node) {
        return description.type.toString().equals(node.getLabel());
    }

    private void mapEdges(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap) {
        for (Edge edge : graph.getEdges()) {
            String dataSourceId = StringUtils.split(edge.getLabel(), GraphExporter.LabelPrefixSeparator)[0];
            MappingDescriber dataSourceDescriber = dataSourceDescriberMap.get(dataSourceId);
            if (dataSourceDescriber != null) {
                EdgeMappingDescription mappingDescription = dataSourceDescriber.describe(graph, edge);
                if (mappingDescription != null) {
                    Long[] mappedFromNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(edge.getFromId(),
                                                                                    MappedToEdgeLabel);
                    Long[] mappedToNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(edge.getToId(), MappedToEdgeLabel);
                    if (mappedFromNodeIds.length > 0 && mappedToNodeIds.length > 0) {
                        Edge mappedEdge = graph.addEdge(mappedFromNodeIds[0], mappedToNodeIds[0],
                                                        mappingDescription.type.name());
                        mappedEdge.setProperty("source", dataSourceId);
                    }
                }
            }
        }
    }

    private void saveGraph(final Graph graph, final String outputGraphFilePath) {
        GraphMLGraphWriter graphMLWriter = new GraphMLGraphWriter();
        graphMLWriter.write(outputGraphFilePath, graph);
    }

    private void saveGraphSchema(final Graph graph, final String filePath) {
        GraphSchema schema = new GraphSchema(graph);
        try {
            new GraphQLSchemaWriter(schema).save(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
