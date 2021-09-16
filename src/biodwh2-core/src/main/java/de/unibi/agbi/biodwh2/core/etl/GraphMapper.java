package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.graphics.MetaGraphImage;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.WorkspaceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.text.MetaGraphDynamicVisWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public final class GraphMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMapper.class);
    private static final String MAPPED_TO_EDGE_LABEL = "MAPPED_TO";
    private static final String IDS_NODE_PROPERTY = "ids";
    private static final String NAMES_NODE_PROPERTY = "names";
    private static final String MAPPED_NODE_PROPERTY = "__mapped";

    public void map(final Workspace workspace, final DataSource[] dataSources) {
        copyGraph(workspace);
        final Path graphFilePath = workspace.getFilePath(WorkspaceFileType.MAPPED_PERSISTENT_GRAPH);
        try (Graph graph = new Graph(graphFilePath, true)) {
            mapGraph(graph, dataSources);
            saveGraph(graph, workspace);
            generateMetaGraphStatistics(graph, workspace);
        }
    }

    private void copyGraph(final Workspace workspace) {
        try {
            Files.copy(workspace.getFilePath(WorkspaceFileType.MERGED_PERSISTENT_GRAPH),
                       workspace.getFilePath(WorkspaceFileType.MAPPED_PERSISTENT_GRAPH),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Failed to copy merged graph to mapped graph file", e);
        }
    }

    void mapGraph(final Graph graph, final DataSource[] dataSources) {
        final Map<String, MappingDescriber> map = getDataSourceDescriberMap(dataSources);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping nodes");
        mapNodes(graph, map);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping paths");
        mapPaths(graph, map);
    }

    private Map<String, MappingDescriber> getDataSourceDescriberMap(final DataSource[] dataSources) {
        final Map<String, MappingDescriber> map = new HashMap<>();
        for (final DataSource dataSource : dataSources)
            map.put(dataSource.getId(), dataSource.getMappingDescriber());
        return map;
    }

    private void mapNodes(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap) {
        final Map<String, Map<String, Long>> labelIdNodeIdMap = new HashMap<>();
        for (final MappingDescriber describer : dataSourceDescriberMap.values()) {
            final String[] localMappingLabels = describer.getNodeMappingLabels();
            if (localMappingLabels != null)
                for (final String localMappingLabel : localMappingLabels)
                    mapNodesWithLabel(graph, labelIdNodeIdMap, describer, localMappingLabel);
        }
    }

    private void mapNodesWithLabel(final Graph graph, final Map<String, Map<String, Long>> labelIdNodeIdMap,
                                   final MappingDescriber describer, final String localMappingLabel) {
        final String prefixedMappingLabel = describer.prefixLabel(localMappingLabel);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping nodes with label '" + prefixedMappingLabel + "'");
        for (final Node node : graph.getNodes(prefixedMappingLabel)) {
            final NodeMappingDescription[] mappingDescriptions = describer.describe(graph, node, localMappingLabel);
            if (mappingDescriptions != null)
                for (final NodeMappingDescription mappingDescription : mappingDescriptions)
                    if (mappingDescription != null) {
                        final Map<String, Long> idNodeIdMap = labelIdNodeIdMap.computeIfAbsent(
                                mappingDescription.getType(), k -> new HashMap<>());
                        mergeMatchingNodes(graph, mappingDescription, idNodeIdMap, node.getId());
                    }
        }
    }

    private void mergeMatchingNodes(final Graph graph, final NodeMappingDescription description,
                                    final Map<String, Long> idNodeIdMap, final long mappedNodeId) {
        final Set<Long> matchedNodeIds = matchNodesFromIds(idNodeIdMap, description);
        final Node mergedNode = mergeOrCreateMappingNode(graph, description, matchedNodeIds);
        graph.addEdge(mappedNodeId, mergedNode, MAPPED_TO_EDGE_LABEL);
        for (final String id : mergedNode.<Collection<String>>getProperty(IDS_NODE_PROPERTY))
            idNodeIdMap.put(id, mergedNode.getId());
    }

    private Set<Long> matchNodesFromIds(final Map<String, Long> idNodeIdMap, final NodeMappingDescription description) {
        final Set<Long> matchedNodeIds = new HashSet<>();
        for (final String id : description.getIdentifiers())
            if (idNodeIdMap.containsKey(id))
                matchedNodeIds.add(idNodeIdMap.get(id));
        return matchedNodeIds;
    }

    private Node mergeOrCreateMappingNode(final Graph graph, final NodeMappingDescription description,
                                          final Set<Long> matchedNodeIds) {
        final Set<String> ids = new HashSet<>(description.getIdentifiers());
        final Set<String> names = description.getNames();
        Node mergedNode = null;
        for (final Long nodeId : matchedNodeIds) {
            final Node matchedNode = graph.getNode(nodeId);
            if (!hasMatchedNodeSameLabel(description, matchedNode))
                continue;
            final Set<String> nodeIds = matchedNode.getProperty(IDS_NODE_PROPERTY);
            ids.addAll(nodeIds);
            final Set<String> nodeNames = matchedNode.getProperty(NAMES_NODE_PROPERTY);
            if (nodeNames != null)
                names.addAll(nodeNames);
            if (mergedNode == null)
                mergedNode = matchedNode;
            else
                graph.mergeNodes(mergedNode, matchedNode);
        }
        if (mergedNode == null) {
            mergedNode = graph.addNode(description.getType(), MAPPED_NODE_PROPERTY, true, IDS_NODE_PROPERTY, ids,
                                       NAMES_NODE_PROPERTY, names);
        } else {
            mergedNode.setProperty(IDS_NODE_PROPERTY, ids);
            mergedNode.setProperty(NAMES_NODE_PROPERTY, names);
            graph.update(mergedNode);
        }
        return mergedNode;
    }

    private boolean hasMatchedNodeSameLabel(final NodeMappingDescription description, final Node node) {
        return description.getType().equals(node.getLabel());
    }

    private void mapPaths(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap) {
        for (final Map.Entry<String, MappingDescriber> entry : dataSourceDescriberMap.entrySet()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Mapping edge paths for data source '" + entry.getKey() + "'");
            for (final PathMapping path : getNonEmptyPathMappingsForDescriber(entry.getValue()))
                mapPath(graph, entry.getValue(), path);
        }
    }

    private List<PathMapping> getNonEmptyPathMappingsForDescriber(final MappingDescriber describer) {
        return Arrays.stream(describer.getEdgePathMappings()).filter(m -> m != null && m.getSegmentCount() > 0).collect(
                Collectors.toList());
    }

    private void mapPath(final Graph graph, final MappingDescriber describer, final PathMapping path) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping edge paths " + path);
        final PathMapping.Segment segment = path.get(0);
        for (final Node node : graph.getNodes(describer.prefixLabel(segment.fromNodeLabel))) {
            final long[] currentPathIds = new long[path.getSegmentCount() * 2 + 1];
            currentPathIds[0] = node.getId();
            buildPathRecursively(graph, describer, path, 0, currentPathIds);
        }
    }

    private void buildPathRecursively(final Graph graph, final MappingDescriber describer, final PathMapping path,
                                      final int segmentIndex, final long[] currentPathIds) {
        if (segmentIndex >= path.getSegmentCount()) {
            mapPathInstance(graph, describer, currentPathIds);
            return;
        }
        final PathMapping.Segment segment = path.get(segmentIndex);
        final String edgeLabel = describer.prefixLabel(segment.edgeLabel);
        final String toNodeLabel = describer.prefixLabel(segment.toNodeLabel);
        final long fromNodeId = currentPathIds[segmentIndex * 2];
        final int currentEdgePathIndex = segmentIndex * 2 + 1;
        if (segment.direction == EdgeDirection.BIDIRECTIONAL ||
            segment.direction == EdgeDirection.FORWARD) {
            for (final Edge edge : graph.findEdges(edgeLabel, Edge.FROM_ID_FIELD, fromNodeId)) {
                final Node nextNode = graph.getNode(edge.getToId());
                if (nextNode.getLabel().equals(toNodeLabel)) {
                    final long[] nextPathIds = Arrays.copyOf(currentPathIds, currentPathIds.length);
                    nextPathIds[currentEdgePathIndex] = edge.getId();
                    nextPathIds[currentEdgePathIndex + 1] = nextNode.getId();
                    buildPathRecursively(graph, describer, path, segmentIndex + 1, nextPathIds);
                }
            }
        }
        if (segment.direction == EdgeDirection.BIDIRECTIONAL ||
            segment.direction == EdgeDirection.BACKWARD) {
            for (final Edge edge : graph.findEdges(edgeLabel, Edge.TO_ID_FIELD, fromNodeId)) {
                final Node nextNode = graph.getNode(edge.getFromId());
                if (nextNode.getLabel().equals(toNodeLabel)) {
                    final long[] nextPathIds = Arrays.copyOf(currentPathIds, currentPathIds.length);
                    nextPathIds[currentEdgePathIndex] = edge.getId();
                    nextPathIds[currentEdgePathIndex + 1] = nextNode.getId();
                    buildPathRecursively(graph, describer, path, segmentIndex + 1, nextPathIds);
                }
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
            for (final Long fromNodeId : mappedFromNodeIds)
                for (final Long toNodeId : mappedToNodeIds)
                    graph.addEdge(fromNodeId, toNodeId, mappingDescription.getType(), "source",
                                  describer.getDataSourceId());
        }
    }

    private void saveGraph(final Graph graph, final Workspace workspace) {
        final Path outputGraphFilePath = workspace.getFilePath(WorkspaceFileType.MAPPED_GRAPHML);
        if (workspace.getConfiguration().shouldSkipGraphMLExport()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Skipping mapped graph GraphML export as per configuration");
            FileUtils.safeDelete(outputGraphFilePath);
            return;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Save mapped graph to GraphML");
        new GraphMLGraphWriter().write(outputGraphFilePath, graph);
    }

    private void generateMetaGraphStatistics(final Graph graph, final Workspace workspace) {
        final Path metaGraphImageFilePath = workspace.getFilePath(WorkspaceFileType.MAPPED_META_GRAPH_IMAGE);
        final Path metaGraphStatsFilePath = workspace.getFilePath(WorkspaceFileType.MAPPED_META_GRAPH_STATISTICS);
        final Path metaGraphDynamicVisFilePath = workspace.getFilePath(WorkspaceFileType.MAPPED_META_GRAPH_DYNAMIC_VIS);
        if (workspace.getConfiguration().shouldSkipMetaGraphGeneration()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Skipping mapped graph meta graph generation as per configuration");
            FileUtils.safeDelete(metaGraphImageFilePath);
            FileUtils.safeDelete(metaGraphStatsFilePath);
            FileUtils.safeDelete(metaGraphDynamicVisFilePath);
            return;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Generating mapped meta graph");
        final MetaGraph metaGraph = new MetaGraph(graph);
        if (metaGraph.getNodeLabelCount() == 0 && metaGraph.getEdgeLabelCount() == 0) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Skipping meta graph image generation of empty meta graph");
            return;
        }
        final String statistics = new MetaGraphStatisticsWriter(metaGraph).write();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(statistics);
            LOGGER.info("Exporting mapped meta graph image to " + metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, 2048, 2048);
        image.drawAndSaveImage(metaGraphImageFilePath);
        FileUtils.writeTextToUTF8File(metaGraphStatsFilePath, statistics);
        final MetaGraphDynamicVisWriter visWriter = new MetaGraphDynamicVisWriter(metaGraph);
        visWriter.write(metaGraphDynamicVisFilePath);
    }
}
