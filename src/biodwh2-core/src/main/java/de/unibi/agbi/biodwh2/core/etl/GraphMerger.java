package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.OntologyDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import de.unibi.agbi.biodwh2.core.graphics.MetaGraphImage;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.model.WorkspaceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.text.MetaGraphDynamicVisWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class GraphMerger {
    private static final Logger LOGGER = LogManager.getLogger(GraphMerger.class);
    private static final int META_GRAPH_IMAGE_SIZE = 2048;

    public void merge(final Workspace workspace, final DataSource[] dataSources) throws MergerException {
        final long start = System.currentTimeMillis();
        final Map<String, DataSourceMetadata> requestedStatus = getRequestedMergeStatus(dataSources);
        final Map<String, DataSourceMetadata> previousStatus = getPreviousMergeStatus(workspace);
        if (isPreviousMergeStatusUsable(requestedStatus, previousStatus)) {
            try {
                mergeFromPreviousStatus(workspace, dataSources, requestedStatus, previousStatus);
            } catch (final MergerException ex) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Merging data sources from previous status failed. Trying merge from scratch.");
                mergeFromScratch(workspace, dataSources);
            }
        } else {
            mergeFromScratch(workspace, dataSources);
        }
        final long stop = System.currentTimeMillis();
        LOGGER.info("Merging finished within {}", DurationFormatUtils.formatDuration(stop - start, "HH:mm:ss.S"));
    }

    private Map<String, DataSourceMetadata> getRequestedMergeStatus(final DataSource[] dataSources) {
        final Map<String, DataSourceMetadata> result = new HashMap<>();
        for (final DataSource dataSource : dataSources) {
            final de.unibi.agbi.biodwh2.core.model.DataSourceMetadata metadata = dataSource.getMetadata();
            result.put(dataSource.getId(),
                       new DataSourceMetadata(dataSource.getId(), dataSource.getOAIId(), metadata.version,
                                              metadata.exportVersion, metadata.exportPropertiesHash));
        }
        return result;
    }

    private Map<String, DataSourceMetadata> getPreviousMergeStatus(final Workspace workspace) {
        final Map<String, DataSourceMetadata> result = new HashMap<>();
        final Path filePath = workspace.getFilePath(WorkspaceFileType.MERGED_PERSISTENT_GRAPH);
        if (filePath.toFile().exists()) {
            // Reopen the existing graph
            try (Graph mergedGraph = new Graph(filePath, true)) {
                for (final Node node : mergedGraph.findNodes("metadata", "type", "datasource")) {
                    final String id = node.getProperty("datasource_id");
                    final Long exportVersion = node.getProperty("export_version");
                    final Integer exportPropertiesHash = node.getProperty("export_properties_hash");
                    result.put(id, new DataSourceMetadata(id, node.getProperty("datasource_oai_id"),
                                                          Version.tryParse(node.getProperty("version")),
                                                          exportVersion != null ? exportVersion : -1,
                                                          exportPropertiesHash));
                }
            } catch (final Exception ex) {
                // Reusing the merged graph is just an optimization, so don't throw and just notify
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Failed to determine previous merge status. Complete merge will be used.", ex);
            }
        }
        return result;
    }

    private boolean isPreviousMergeStatusUsable(final Map<String, DataSourceMetadata> requestedStatus,
                                                final Map<String, DataSourceMetadata> previousStatus) {
        // TODO: currently not usable due to dependencies
        return false;
        // final Set<String> intersection = new HashSet<>(requestedStatus.keySet());
        // intersection.retainAll(previousStatus.keySet());
        // // If we have a previous state and any intersection between old and newly requested data sources, it's usable
        // return !previousStatus.isEmpty() && !intersection.isEmpty();
    }

    private void mergeFromScratch(final Workspace workspace, final DataSource[] dataSources) throws MergerException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Creating new merged graph from scratch");
        try (Graph mergedGraph = new Graph(workspace.getFilePath(WorkspaceFileType.MERGED_PERSISTENT_GRAPH))) {
            final Map<Long, Long> dependencyNodeIdMap = new HashMap<>();
            for (final DataSource dataSource : dataSources)
                mergeDataSource(workspace, dataSource, mergedGraph, dependencyNodeIdMap);
            saveMergedGraph(workspace, mergedGraph);
            generateMetaGraphStatistics(mergedGraph, workspace);
        } catch (final Exception ex) {
            throw new MergerException(ex);
        }
    }

    private void mergeDataSource(final Workspace workspace, final DataSource dataSource, final Graph mergedGraph,
                                 final Map<Long, Long> dependencyNodeIdMap) throws MergerException {
        final Path intermediateGraphFilePath = dataSource.getFilePath(workspace, DataSourceFileType.PERSISTENT_GRAPH);
        if (!intermediateGraphFilePath.toFile().exists())
            throw new MergerException(
                    "Failed to merge data source " + dataSource.getId() + " because the exported graph is missing");
        try (Graph databaseToMerge = new Graph(intermediateGraphFilePath, true, true)) {
            final long numberOfNodes = databaseToMerge.getNumberOfNodes();
            final long numberOfEdges = databaseToMerge.getNumberOfEdges();
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Merging data source '{}' [{} nodes, {} edges]", dataSource.getId(), numberOfNodes,
                            numberOfEdges);
            if (dataSource instanceof OntologyDataSource) {
                final var mapping = mergedGraph.mergeDatabase(dataSource.getId(), databaseToMerge,
                                                              getNodeProgressLogger(numberOfNodes),
                                                              getEdgeProgressLogger(numberOfEdges));
                dependencyNodeIdMap.putAll(mapping);
            } else {
                mergedGraph.mergeDatabaseComplex(dataSource.getId(), databaseToMerge,
                                                 getNodeProgressLogger(numberOfNodes),
                                                 getEdgeProgressLogger(numberOfEdges),
                                                 GraphExporter.DEPENDENCY_NODE_PROPERTY, dependencyNodeIdMap);
            }
        } catch (GraphCacheException e) {
            throw new MergerException("Failed to merge data source " + dataSource.getId(), e);
        }
        addDataSourceMetadataNode(dataSource, mergedGraph);
    }

    private static Consumer<Long> getNodeProgressLogger(final long total) {
        return (progress) -> {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("\tNode progress: {}", TextUtils.getProgressText(progress, total));
        };
    }

    private static Consumer<Long> getEdgeProgressLogger(final long total) {
        return (progress) -> {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("\tEdge progress: {}", TextUtils.getProgressText(progress, total));
        };
    }

    private void addDataSourceMetadataNode(final DataSource dataSource, final Graph graph) {
        final de.unibi.agbi.biodwh2.core.model.DataSourceMetadata metadata = dataSource.getMetadata();
        final NodeBuilder builder = graph.buildNode().withLabel("metadata");
        builder.withProperty("type", "datasource");
        builder.withProperty("datasource_id", dataSource.getId());
        builder.withProperty("datasource_oai_id", dataSource.getOAIId());
        builder.withProperty("version", metadata.version != null ? metadata.version.toString() : "");
        builder.withProperty("export_version", metadata.exportVersion);
        builder.withProperty("export_properties_hash", metadata.exportPropertiesHash);
        builder.withPropertyIfNotNull("license", dataSource.getLicense());
        builder.withPropertyIfNotNull("license_url", dataSource.getLicenseUrl());
        builder.build();
    }

    private void saveMergedGraph(final Workspace workspace, final Graph mergedGraph) {
        final Path outputGraphFilePath = workspace.getFilePath(WorkspaceFileType.MERGED_GRAPHML_GZ);
        if (workspace.getConfiguration().shouldSkipGraphMLExport()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Skipping merged graph GraphML export as per configuration");
            FileUtils.safeDelete(outputGraphFilePath);
            return;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Save merged graph to GraphML");
        new GraphMLGraphWriter().write(outputGraphFilePath, mergedGraph);
    }

    private void generateMetaGraphStatistics(final Graph graph, final Workspace workspace) {
        final Path metaGraphImageFilePath = workspace.getFilePath(WorkspaceFileType.MERGED_META_GRAPH_IMAGE);
        final Path metaGraphStatsFilePath = workspace.getFilePath(WorkspaceFileType.MERGED_META_GRAPH_STATISTICS);
        final Path metaGraphDynamicVisFilePath = workspace.getFilePath(WorkspaceFileType.MERGED_META_GRAPH_DYNAMIC_VIS);
        if (workspace.getConfiguration().shouldSkipMetaGraphGeneration()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Skipping merged graph meta graph generation as per configuration");
            FileUtils.safeDelete(metaGraphImageFilePath);
            FileUtils.safeDelete(metaGraphStatsFilePath);
            FileUtils.safeDelete(metaGraphDynamicVisFilePath);
            return;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Generating merged meta graph");
        final MetaGraph metaGraph = new MetaGraph(graph);
        if (metaGraph.getNodeLabelCount() == 0 && metaGraph.getEdgeLabelCount() == 0) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Skipping meta graph image generation of empty meta graph");
            return;
        }
        final String statistics = new MetaGraphStatisticsWriter(metaGraph).write();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(statistics);
            LOGGER.info("Exporting merged meta graph image to {}", metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, META_GRAPH_IMAGE_SIZE, META_GRAPH_IMAGE_SIZE);
        image.drawAndSaveImage(metaGraphImageFilePath);
        FileUtils.writeTextToUTF8File(metaGraphStatsFilePath, statistics);
        final MetaGraphDynamicVisWriter visWriter = new MetaGraphDynamicVisWriter(metaGraph);
        visWriter.write(metaGraphDynamicVisFilePath);
    }

    private void mergeFromPreviousStatus(final Workspace workspace, final DataSource[] dataSources,
                                         final Map<String, DataSourceMetadata> requestedStatus,
                                         final Map<String, DataSourceMetadata> previousStatus) throws MergerException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Creating merged graph from previous status");
        try (Graph mergedGraph = new Graph(workspace.getFilePath(WorkspaceFileType.MERGED_PERSISTENT_GRAPH), true)) {
            final Map<Long, Long> dependencyNodeIdMap = new HashMap<>();
            // TODO: re-merge dependencies
            // First, remove all previous states which are not requested anymore
            for (final String id : previousStatus.keySet())
                if (!requestedStatus.containsKey(id))
                    removePreviousDataSourceVersion(mergedGraph, id);
            for (final DataSource dataSource : dataSources) {
                // Only add newly requested data sources or remove and re-add outdated ones
                if (!previousStatus.containsKey(dataSource.getId()))
                    mergeDataSource(workspace, dataSource, mergedGraph, dependencyNodeIdMap);
                else {
                    final DataSourceMetadata metadata = previousStatus.get(dataSource.getId());
                    final DataSourceMetadata requestedMetadata = requestedStatus.get(metadata.id);
                    if (metadata.exportPropertiesHash == null || !metadata.exportPropertiesHash.equals(
                            requestedMetadata.exportPropertiesHash) ||
                        requestedMetadata.exportVersion > metadata.exportVersion || requestedMetadata.version.compareTo(
                            metadata.version) > 0 || workspace.isDataSourceExportForced(dataSource)) {
                        removePreviousDataSourceVersion(mergedGraph, dataSource.getId());
                        mergeDataSource(workspace, dataSource, mergedGraph, dependencyNodeIdMap);
                    }
                }
            }
            saveMergedGraph(workspace, mergedGraph);
            generateMetaGraphStatistics(mergedGraph, workspace);
        } catch (final Exception ex) {
            throw new MergerException(ex);
        }
    }

    private void removePreviousDataSourceVersion(final Graph mergedGraph, final String dataSourceId) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Removing previously merged version of data source '{}'", dataSourceId);
        for (final String label : mergedGraph.getNodeLabels())
            if (label.startsWith(dataSourceId + Graph.LABEL_PREFIX_SEPARATOR))
                mergedGraph.removeNodeLabel(label);
        for (final String label : mergedGraph.getEdgeLabels())
            if (label.startsWith(dataSourceId + Graph.LABEL_PREFIX_SEPARATOR))
                mergedGraph.removeEdgeLabel(label);
        final Node metadataNode = mergedGraph.findNode("metadata", "type", "datasource", "datasource_id", dataSourceId);
        if (metadataNode != null)
            mergedGraph.removeNode(metadataNode);
    }

    private static class DataSourceMetadata {
        public final String id;
        public final String oaiId;
        public final Version version;
        public final Long exportVersion;
        public final Integer exportPropertiesHash;

        private DataSourceMetadata(final String id, String oaiId, final Version version, final Long exportVersion,
                                   final Integer exportPropertiesHash) {
            this.id = id;
            this.oaiId = oaiId;
            this.version = version;
            this.exportVersion = exportVersion;
            this.exportPropertiesHash = exportPropertiesHash;
        }
    }
}
