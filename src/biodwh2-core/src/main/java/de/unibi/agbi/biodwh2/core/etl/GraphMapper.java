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

public final class GraphMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMapper.class);
    private static final String MAPPED_TO_EDGE_LABEL = "MAPPED_TO";
    private static final String IDS_NODE_PROPERTY = "ids";
    private static final String MAPPED_NODE_PROPERTY = "__mapped";

    public void map(final Workspace workspace, final DataSource[] dataSources, final String inputGraphFilePath,
                    final String outputGraphFilePath) {
        copyGraph(inputGraphFilePath, outputGraphFilePath);
        final Graph graph = new Graph(outputGraphFilePath.replace(GraphFileFormat.GRAPH_ML.extension, Graph.EXTENSION),
                                      true);
        mapGraph(graph, dataSources);
        saveGraph(graph, outputGraphFilePath);
        saveGraphSchema(graph,
                        outputGraphFilePath.replace(GraphFileFormat.GRAPH_ML.extension, GraphQLSchemaWriter.EXTENSION));
        graph.dispose();
    }

    private void copyGraph(final String inputGraphFilePath, final String outputGraphFilePath) {
        final Path originalPath = Paths.get(
                inputGraphFilePath.replace(GraphFileFormat.GRAPH_ML.extension, Graph.EXTENSION));
        final Path copied = Paths.get(outputGraphFilePath.replace(GraphFileFormat.GRAPH_ML.extension, Graph.EXTENSION));
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to copy merged graph to mapped graph file", e);
        }
    }

    private void mapGraph(final Graph graph, final DataSource[] dataSources) {
        final Map<String, MappingDescriber> map = getDataSourceDescriberMap(dataSources);
        mapNodes(graph, map);
        mapPaths(graph, map);
    }

    private Map<String, MappingDescriber> getDataSourceDescriberMap(final DataSource[] dataSources) {
        final Map<String, MappingDescriber> map = new HashMap<>();
        for (final DataSource dataSource : dataSources)
            map.put(dataSource.getId(), dataSource.getMappingDescriber());
        return map;
    }

    private void mapNodes(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap) {
        final Map<String, Set<Long>> idNodeIdMap = new HashMap<>();
        for (final MappingDescriber describer : dataSourceDescriberMap.values()) {
            final String[] mappingLabels = describer.getPrefixedNodeMappingLabels();
            for (final String mappingLabel : mappingLabels) {
                for (final Node node : graph.getNodes(mappingLabel)) {
                    final NodeMappingDescription mappingDescription = describer.describe(graph, node);
                    if (mappingDescription != null)
                        mergeMatchingNodes(graph, mappingDescription, idNodeIdMap, node.getId());
                }
            }
        }
    }

    private String getDataSourceIdFromLabel(final String label) {
        return StringUtils.split(label, GraphExporter.LABEL_PREFIX_SEPARATOR)[0];
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
        mergedNode.setProperty(MAPPED_NODE_PROPERTY, true);
        mergedNode.setProperty(IDS_NODE_PROPERTY, ids.toArray(new String[0]));
        graph.update(mergedNode);
        return mergedNode;
    }

    private boolean hasMatchedNodeSameLabel(final NodeMappingDescription description, final Node node) {
        return description.type.toString().equals(node.getLabel());
    }

    private void mapPaths(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap) {
        for (final MappingDescriber describer : dataSourceDescriberMap.values())
            for (final String[] path : describer.getPrefixedEdgeMappingPaths())
                mapPath(graph, describer, path);
    }

    private void mapPath(final Graph graph, final MappingDescriber describer, final String[] path) {
        for (Node node : graph.getNodes(path[0])) {
            final long[] currentPathIds = new long[path.length];
            currentPathIds[0] = node.getId();
            buildPathRecursively(graph, describer, path, 1, currentPathIds);
        }
    }

    private void buildPathRecursively(final Graph graph, final MappingDescriber describer, final String[] path,
                                      final int edgeIndex, final long[] currentPathIds) {
        if (edgeIndex >= path.length) {
            mapPathInstance(graph, describer, currentPathIds);
            return;
        }
        for (final Edge edge : graph.findEdges(path[edgeIndex], Edge.FROM_ID_FIELD, currentPathIds[edgeIndex - 1])) {
            currentPathIds[edgeIndex] = edge.getId();
            final Node nextNode = graph.getNode(edge.getToId());
            if (nextNode.getLabel().equals(path[edgeIndex + 1])) {
                final long[] nextPathIds = Arrays.copyOf(currentPathIds, currentPathIds.length);
                nextPathIds[edgeIndex + 1] = nextNode.getId();
                buildPathRecursively(graph, describer, path, edgeIndex + 2, nextPathIds);
            }
        }
        for (final Edge edge : graph.findEdges(path[edgeIndex], Edge.TO_ID_FIELD, currentPathIds[edgeIndex - 1])) {
            currentPathIds[edgeIndex] = edge.getId();
            final Node nextNode = graph.getNode(edge.getFromId());
            if (nextNode.getLabel().equals(path[edgeIndex + 1])) {
                final long[] nextPathIds = Arrays.copyOf(currentPathIds, currentPathIds.length);
                nextPathIds[edgeIndex + 1] = nextNode.getId();
                buildPathRecursively(graph, describer, path, edgeIndex + 2, nextPathIds);
            }
        }
    }

    private void mapPathInstance(final Graph graph, final MappingDescriber describer, final long[] pathIds) {
        final Node[] nodes = new Node[pathIds.length / 2 + 1];
        for (int i = 0; i < nodes.length; i++)
            nodes[i] = graph.getNode(pathIds[i * 2]);
        final Edge[] edges = new Edge[pathIds.length / 2];
        for (int i = 0; i < edges.length; i++)
            edges[i] = graph.getEdge(pathIds[i * 2 + 1]);
        final PathMappingDescription mappingDescription = describer.describe(graph, nodes, edges);
        if (mappingDescription != null) {
            final Long[] mappedFromNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(pathIds[0], MAPPED_TO_EDGE_LABEL);
            final Long[] mappedToNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(pathIds[pathIds.length - 1],
                                                                                MAPPED_TO_EDGE_LABEL);
            if (mappedFromNodeIds.length > 0 && mappedToNodeIds.length > 0) {
                final Edge mappedEdge = graph.addEdge(mappedFromNodeIds[0], mappedToNodeIds[0],
                                                      mappingDescription.type.name());
                mappedEdge.setProperty("source", describer.getDataSourceId());
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
