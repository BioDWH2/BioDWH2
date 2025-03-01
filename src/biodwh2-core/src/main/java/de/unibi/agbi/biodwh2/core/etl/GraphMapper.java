package de.unibi.agbi.biodwh2.core.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.graphics.MetaGraphImage;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.SerializableUtils;
import de.unibi.agbi.biodwh2.core.model.WorkspaceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.text.MetaGraphDynamicVisWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphExtendedStatisticsWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class GraphMapper {
    private static final Logger LOGGER = LogManager.getLogger(GraphMapper.class);
    private static final String MERGED_INTO_EDGE_LABEL = "MERGED_INTO";
    private static final String MAPPED_TO_EDGE_LABEL = "MAPPED_TO";
    private static final String IDS_NODE_PROPERTY = "ids";
    private static final String NAMES_NODE_PROPERTY = "names";
    private static final String MAPPED_NODE_PROPERTY = "__mapped";

    public void map(final Workspace workspace, final DataSource[] dataSources, final boolean runsInParallel,
                    final int numThreads) {
        copyGraph(workspace);
        final Path graphFilePath = workspace.getFilePath(WorkspaceFileType.MAPPED_PERSISTENT_GRAPH);
        final Path logGraphFilePath = workspace.getFilePath(WorkspaceFileType.MAPPED_LOG_PERSISTENT_GRAPH);
        try (final Graph graph = new Graph(graphFilePath, true); final Graph logGraph = new Graph(logGraphFilePath)) {
            final long start = System.currentTimeMillis();
            mapGraph(graph, logGraph, dataSources, runsInParallel, numThreads);
            final long stop = System.currentTimeMillis();
            LOGGER.info("Mapping finished within {}", DurationFormatUtils.formatDuration(stop - start, "HH:mm:ss.S"));
            saveGraph(graph, workspace);
            generateMetaGraphStatistics(graph, workspace);
            generateLogGraphClusters(logGraph, workspace);
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

    void mapGraph(final Graph graph, final Graph logGraph, final DataSource[] dataSources, final boolean runsInParallel,
                  final int numThreads) {
        final Map<String, MappingDescriber> map = getDataSourceDescriberMap(dataSources);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping nodes");
        mapNodes(graph, logGraph, map);
        if (LOGGER.isInfoEnabled())
            if (runsInParallel) {
                LOGGER.info("Starting path mapping in parallel mode with {} threads", numThreads);
            } else {
                LOGGER.info("Starting path mapping in serial mode");
            }
        mapPaths(graph, map, runsInParallel, numThreads);
    }

    private Map<String, MappingDescriber> getDataSourceDescriberMap(final DataSource[] dataSources) {
        final Map<String, MappingDescriber> map = new HashMap<>();
        for (final DataSource dataSource : dataSources)
            map.put(dataSource.getId(), dataSource.getMappingDescriber());
        return map;
    }

    private void mapNodes(final Graph graph, final Graph logGraph,
                          final Map<String, MappingDescriber> dataSourceDescriberMap) {
        final Map<String, Map<String, Long>> labelIdNodeIdMap = new HashMap<>();
        for (final MappingDescriber describer : dataSourceDescriberMap.values()) {
            final String[] localMappingLabels = describer.getNodeMappingLabels();
            if (localMappingLabels != null)
                for (final String localMappingLabel : localMappingLabels)
                    mapNodesWithLabel(graph, logGraph, labelIdNodeIdMap, describer, localMappingLabel);
        }
    }

    private void mapNodesWithLabel(final Graph graph, final Graph logGraph,
                                   final Map<String, Map<String, Long>> labelIdNodeIdMap,
                                   final MappingDescriber describer, final String localMappingLabel) {
        final String prefixedMappingLabel = describer.prefixLabel(localMappingLabel);
        if (graph.getNumberOfNodes(prefixedMappingLabel) == 0)
            return;
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping nodes with label '{}'", prefixedMappingLabel);
        for (final Node node : graph.getNodes(prefixedMappingLabel)) {
            final NodeMappingDescription[] mappingDescriptions = describer.describe(graph, node, localMappingLabel);
            if (mappingDescriptions != null)
                for (final NodeMappingDescription mappingDescription : mappingDescriptions)
                    if (mappingDescription != null) {
                        final Map<String, Long> idNodeIdMap = labelIdNodeIdMap.computeIfAbsent(
                                mappingDescription.getType(), k -> new HashMap<>());
                        mergeMatchingNodes(graph, logGraph, mappingDescription, idNodeIdMap, node);
                    }
        }
    }

    private void mergeMatchingNodes(final Graph graph, final Graph logGraph, final NodeMappingDescription description,
                                    final Map<String, Long> idNodeIdMap, final Node mappedNode) {
        final Set<Long> matchedNodeIds = matchNodesFromIds(idNodeIdMap, description);
        final Node mergedNode = mergeOrCreateMappingNode(graph, description, matchedNodeIds);
        graph.addEdge(mappedNode.getId(), mergedNode, MAPPED_TO_EDGE_LABEL);
        final Set<String> nodeIds = mergedNode.getProperty(IDS_NODE_PROPERTY);
        if (nodeIds != null)
            for (final String id : nodeIds)
                idNodeIdMap.put(id, mergedNode.getId());
        // Export logging
        final Node logMergedNode = SerializableUtils.clone(mergedNode);
        if (logMergedNode != null) {
            final Node sourceLogNode = createMappingNode(logGraph, mappedNode.getLabel(), description.getIdentifiers(),
                                                         description.getNames(), description.getAdditionalProperties());
            sourceLogNode.setProperty("source_node_id", mappedNode.getId());
            sourceLogNode.setProperty(MAPPED_NODE_PROPERTY, false);
            logGraph.update(sourceLogNode);
            matchedNodeIds.add(sourceLogNode.getId());
            logGraph.update(logMergedNode);
            for (final Long id : matchedNodeIds)
                if (!Objects.equals(id, logMergedNode.getId()))
                    logGraph.addEdge(id, logMergedNode, MERGED_INTO_EDGE_LABEL);
        }
    }

    private Set<Long> matchNodesFromIds(final Map<String, Long> idNodeIdMap, final NodeMappingDescription description) {
        final Set<Long> matchedNodeIds = new HashSet<>();
        for (final String id : description.getIdentifiers()) {
            final Long nodeId = idNodeIdMap.get(id);
            if (nodeId != null)
                matchedNodeIds.add(nodeId);
        }
        return matchedNodeIds;
    }

    private Node mergeOrCreateMappingNode(final Graph graph, final NodeMappingDescription description,
                                          final Set<Long> matchedNodeIds) {
        final Set<String> ids = new HashSet<>(description.getIdentifiers());
        final Set<String> names = description.getNames();
        final Map<String, Object> additionalProperties = description.getAdditionalProperties();
        Node mergedNode = null;
        for (final Long nodeId : matchedNodeIds) {
            final Node matchedNode = graph.getNode(nodeId);
            if (matchedNode == null || !hasMatchedNodeSameLabel(description, matchedNode))
                continue;
            final Set<String> nodeIds = matchedNode.getProperty(IDS_NODE_PROPERTY);
            if (nodeIds != null)
                ids.addAll(nodeIds);
            final Set<String> nodeNames = matchedNode.getProperty(NAMES_NODE_PROPERTY);
            if (nodeNames != null)
                names.addAll(nodeNames);
            if (additionalProperties != null) {
                for (final String key : additionalProperties.keySet()) {
                    final Object value = additionalProperties.get(key);
                    final Object matchedValue = matchedNode.getProperty(key);
                    if (matchedValue != null) {
                        if (value == null)
                            additionalProperties.put(key, matchedValue);
                            // TODO: equals for collections/arrays
                        else if (!matchedValue.equals(value)) {
                            // TODO: handle mismatch!
                            LOGGER.warn("Mismatch in mapping additional property '{}': '{}' <> '{}'", key, value,
                                        matchedValue);
                        }
                    }
                }
            }
            if (mergedNode == null)
                mergedNode = matchedNode;
            else
                graph.mergeNodes(mergedNode, matchedNode);
        }
        if (mergedNode == null) {
            mergedNode = createMappingNode(graph, description.getType(), ids, names, additionalProperties);
        } else {
            mergedNode.setProperty(IDS_NODE_PROPERTY, ids);
            mergedNode.setProperty(NAMES_NODE_PROPERTY, names);
            if (additionalProperties != null) {
                for (final String key : additionalProperties.keySet()) {
                    final Object value = additionalProperties.get(key);
                    if (value != null)
                        mergedNode.setProperty(key, value);

                }
            }
            graph.update(mergedNode);
        }
        return mergedNode;
    }

    private Node createMappingNode(final Graph graph, final String label, final Set<String> ids,
                                   final Set<String> names, final Map<String, Object> additionalProperties) {
        final NodeBuilder builder = graph.buildNode().withLabel(label);
        builder.withProperty(MAPPED_NODE_PROPERTY, true);
        builder.withProperty(IDS_NODE_PROPERTY, ids);
        builder.withProperty(NAMES_NODE_PROPERTY, names);
        if (additionalProperties != null)
            for (final String key : additionalProperties.keySet())
                builder.withPropertyIfNotNull(key, additionalProperties.get(key));
        return builder.build();
    }

    private boolean hasMatchedNodeSameLabel(final NodeMappingDescription description, final Node node) {
        return description.getType().equals(node.getLabel());
    }

    private void mapPaths(final Graph graph, final Map<String, MappingDescriber> dataSourceDescriberMap,
                          final boolean runsInParallel, final int numThreads) {
        for (final Map.Entry<String, MappingDescriber> entry : dataSourceDescriberMap.entrySet()) {
            final List<PathMapping> pathMappings = getNonEmptyPathMappingsForDescriber(entry.getValue());
            if (pathMappings == null || pathMappings.isEmpty())
                continue;
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Mapping edge paths for data source '{}'", entry.getKey());
            for (final PathMapping path : pathMappings) {
                final ConcurrentHashMap<String, Boolean> mappedEdgeTypes = new ConcurrentHashMap<>();
                mapPath(graph, entry.getValue(), path, runsInParallel, numThreads, mappedEdgeTypes);
                for (final String label : mappedEdgeTypes.keySet())
                    graph.endEdgeIndicesDelay(label);
            }
        }
    }

    private List<PathMapping> getNonEmptyPathMappingsForDescriber(final MappingDescriber describer) {
        final PathMapping[] pathMappings = describer.getEdgePathMappings();
        return pathMappings == null ? Collections.emptyList() : Arrays.stream(pathMappings).filter(
                m -> m != null && m.getSegmentCount() > 0).collect(Collectors.toList());
    }

    private void mapPath(final Graph graph, final MappingDescriber describer, final PathMapping path,
                         final boolean runsInParallel, final int numThreads,
                         final AbstractMap<String, Boolean> mappedEdgeTypes) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Mapping edge paths {}",
                        path.toString(describer.getDataSourceId() + Graph.LABEL_PREFIX_SEPARATOR));
        final PathMapping.Segment segment = path.get(0);
        if (runsInParallel) {
            ForkJoinPool pool = null;
            try {
                pool = new ForkJoinPool(numThreads);
                final Spliterator<Long> nodeIds = graph.getNodeIds(describer.prefixLabel(segment.fromNodeLabel))
                                                       .spliterator();
                pool.submit(() -> StreamSupport.stream(nodeIds, true).parallel().forEach(
                        nodeId -> startBuildPathRecursively(graph, describer, path, nodeId, mappedEdgeTypes)));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (pool != null)
                    pool.shutdown();
            }
        } else
            for (final Long nodeId : graph.getNodeIds(describer.prefixLabel(segment.fromNodeLabel)))
                startBuildPathRecursively(graph, describer, path, nodeId, mappedEdgeTypes);
    }

    private void startBuildPathRecursively(final Graph graph, final MappingDescriber describer, final PathMapping path,
                                           final long nodeId, final AbstractMap<String, Boolean> mappedEdgeTypes) {
        final long[] currentPathIds = new long[path.getSegmentCount() * 2 + 1];
        currentPathIds[0] = nodeId;
        buildPathRecursively(graph, describer, path, 0, currentPathIds, mappedEdgeTypes);
    }

    private void buildPathRecursively(final Graph graph, final MappingDescriber describer, final PathMapping path,
                                      final int segmentIndex, final long[] currentPathIds,
                                      final AbstractMap<String, Boolean> mappedEdgeTypes) {
        if (segmentIndex >= path.getSegmentCount()) {
            mapPathInstance(graph, describer, currentPathIds, mappedEdgeTypes);
            return;
        }
        final PathMapping.Segment segment = path.get(segmentIndex);
        final String edgeLabel = describer.prefixLabel(segment.edgeLabel);
        final String toNodeLabel = describer.prefixLabel(segment.toNodeLabel);
        final long fromNodeId = currentPathIds[segmentIndex * 2];
        final int currentEdgePathIndex = segmentIndex * 2 + 1;
        if (segment.direction == EdgeDirection.BIDIRECTIONAL || segment.direction == EdgeDirection.FORWARD) {
            for (final Edge edge : graph.findEdges(edgeLabel, Edge.FROM_ID_FIELD, fromNodeId)) {
                final String nextNodeLabel = graph.getNodeLabel(edge.getToId());
                if (toNodeLabel.equals(nextNodeLabel)) {
                    // Don't walk back edges already traversed
                    if (ArrayUtils.indexOf(currentPathIds, edge.getId()) == ArrayUtils.INDEX_NOT_FOUND) {
                        final long[] nextPathIds = Arrays.copyOf(currentPathIds, currentPathIds.length);
                        nextPathIds[currentEdgePathIndex] = edge.getId();
                        nextPathIds[currentEdgePathIndex + 1] = edge.getToId();
                        buildPathRecursively(graph, describer, path, segmentIndex + 1, nextPathIds, mappedEdgeTypes);
                    }
                }
            }
        }
        if (segment.direction == EdgeDirection.BIDIRECTIONAL || segment.direction == EdgeDirection.BACKWARD) {
            for (final Edge edge : graph.findEdges(edgeLabel, Edge.TO_ID_FIELD, fromNodeId)) {
                final String nextNodeLabel = graph.getNodeLabel(edge.getFromId());
                if (toNodeLabel.equals(nextNodeLabel)) {
                    // Don't walk back edges already traversed
                    if (ArrayUtils.indexOf(currentPathIds, edge.getId()) == ArrayUtils.INDEX_NOT_FOUND) {
                        final long[] nextPathIds = Arrays.copyOf(currentPathIds, currentPathIds.length);
                        nextPathIds[currentEdgePathIndex] = edge.getId();
                        nextPathIds[currentEdgePathIndex + 1] = edge.getFromId();
                        buildPathRecursively(graph, describer, path, segmentIndex + 1, nextPathIds, mappedEdgeTypes);
                    }
                }
            }
        }
    }

    private void mapPathInstance(final Graph graph, final MappingDescriber describer, final long[] pathIds,
                                 final AbstractMap<String, Boolean> mappedEdgeTypes) {
        final Node[] nodes = new Node[pathIds.length / 2 + 1];
        for (int i = 0; i < nodes.length; i++)
            nodes[i] = graph.getNode(pathIds[i * 2]);
        final Edge[] edges = new Edge[pathIds.length / 2];
        final long[] edgeIds = new long[edges.length];
        for (int i = 0; i < edges.length; i++) {
            edgeIds[i] = pathIds[i * 2 + 1];
            edges[i] = graph.getEdge(edgeIds[i]);
        }
        final PathMappingDescription description = describer.describe(graph, nodes, edges);
        if (description != null) {
            final String mappedLabel = description.getType();
            final Map<String, Object> additionalProperties = description.getAdditionalProperties();
            if (!mappedEdgeTypes.containsKey(mappedLabel)) {
                mappedEdgeTypes.put(mappedLabel, true);
                graph.beginEdgeIndicesDelay(mappedLabel);
            }
            final Long[] mappedFromNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(pathIds[0], MAPPED_TO_EDGE_LABEL);
            final Long[] mappedToNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(pathIds[pathIds.length - 1],
                                                                                MAPPED_TO_EDGE_LABEL);
            final var builder = graph.buildEdge().withLabel(mappedLabel);
            builder.withProperty("source", describer.getDataSourceId());
            builder.withProperty("path_edge_ids", edgeIds);
            if (additionalProperties != null)
                for (final String key : additionalProperties.keySet())
                    builder.withPropertyIfNotNull(key, additionalProperties.get(key));
            // Reuse builder for all from/to node combinations
            for (final Long fromNodeId : mappedFromNodeIds)
                for (final Long toNodeId : mappedToNodeIds)
                    builder.fromNode(fromNodeId).toNode(toNodeId).build();
        }
    }

    private void saveGraph(final Graph graph, final Workspace workspace) {
        // TODO: remove old and unused
        for (final var writer : workspace.getOutputFormatWriters()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting mapped graph to {}", writer.getId());
            writer.write(workspace, "mapped", graph);
        }
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
            LOGGER.info("For extended meta graph information see: {}", metaGraphStatsFilePath);
            LOGGER.info("Exporting mapped meta graph image to {}", metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, 2048, 2048);
        image.drawAndSaveImage(metaGraphImageFilePath);
        new MetaGraphExtendedStatisticsWriter(metaGraph).write(metaGraphStatsFilePath);
        final MetaGraphDynamicVisWriter visWriter = new MetaGraphDynamicVisWriter(metaGraph);
        visWriter.write(metaGraphDynamicVisFilePath);
    }

    private void generateLogGraphClusters(final Graph logGraph, final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting mapping log graph clusters");
        final Path clustersFilePath = workspace.getFilePath(WorkspaceFileType.MAPPED_LOG_CLUSTERS);
        final ObjectMapper mapper = new ObjectMapper();
        try (final SequenceWriter writer = mapper.writer().withRootValueSeparator("\n").writeValues(
                clustersFilePath.toFile())) {
            for (final Node node : logGraph.getNodes()) {
                final Long[] forwardNodeIds = logGraph.getAdjacentNodeIdsForEdgeLabel(node.getId(),
                                                                                      MERGED_INTO_EDGE_LABEL,
                                                                                      EdgeDirection.FORWARD);
                // If we find a final node without outgoing edges collect the cluster
                if (forwardNodeIds.length == 0) {
                    final ObjectNode jsonNode = createJsonLogNodeRecursive(logGraph, node, mapper);
                    writer.write(jsonNode);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to export log graph clusters", e);
        }
    }

    private ObjectNode createJsonLogNodeRecursive(final Graph logGraph, final Node node, final ObjectMapper mapper) {
        final ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put("id", node.getId());
        jsonNode.put("label", node.getLabel());
        final Set<String> ids = node.getProperty(IDS_NODE_PROPERTY);
        final Set<String> names = node.getProperty(NAMES_NODE_PROPERTY);
        final ArrayNode idsArray = jsonNode.putArray(IDS_NODE_PROPERTY);
        if (ids != null)
            for (final String id : ids)
                idsArray.add(id);
        final ArrayNode namesArray = jsonNode.putArray(NAMES_NODE_PROPERTY);
        if (names != null)
            for (final String name : names)
                namesArray.add(name);
        jsonNode.put(MAPPED_NODE_PROPERTY, node.<Boolean>getProperty(MAPPED_NODE_PROPERTY));
        final ArrayNode childrenArray = jsonNode.putArray("children");
        for (final Long nodeId : logGraph.getAdjacentNodeIdsForEdgeLabel(node.getId(), MERGED_INTO_EDGE_LABEL,
                                                                         EdgeDirection.BACKWARD)) {
            final Node childNode = logGraph.getNode(nodeId);
            if (childNode != null)
                childrenArray.add(createJsonLogNodeRecursive(logGraph, childNode, mapper));
        }
        return jsonNode;
    }
}
