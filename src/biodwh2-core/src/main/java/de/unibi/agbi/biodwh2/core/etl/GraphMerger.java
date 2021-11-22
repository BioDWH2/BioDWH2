package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
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
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.text.MetaGraphDynamicVisWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class GraphMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMerger.class);

    public void merge(final Workspace workspace, final DataSource[] dataSources) throws MergerException {
        final Map<String, DataSourceMetadata> requestedStatus = getRequestedMergeStatus(dataSources);
        final Map<String, DataSourceMetadata> previousStatus = getPreviousMergeStatus(workspace);
        if (isPreviousMergeStatusUsable(workspace, requestedStatus, previousStatus))
            mergeFromPreviousStatus(workspace, dataSources, previousStatus);
        else
            mergeFromScratch(workspace, dataSources);
    }

    private Map<String, DataSourceMetadata> getRequestedMergeStatus(final DataSource[] dataSources) {
        final Map<String, DataSourceMetadata> result = new HashMap<>();
        for (final DataSource dataSource : dataSources)
            result.put(dataSource.getId(), new DataSourceMetadata(dataSource.getId(), dataSource.getMetadata().version,
                                                                  dataSource.getMetadata().exportVersion));
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
                    result.put(id, new DataSourceMetadata(id, Version.tryParse(node.getProperty("version")),
                                                          exportVersion != null ? exportVersion : -1));
                }
            } catch (final Exception ex) {
                // Reusing the merged graph is just an optimization, so don't throw and just notify
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Failed to determine previous merge status. Complete merge will be used.", ex);
            }
        }
        return result;
    }

    private boolean isPreviousMergeStatusUsable(final Workspace workspace,
                                                final Map<String, DataSourceMetadata> requestedStatus,
                                                final Map<String, DataSourceMetadata> previousStatus) {
        // Old merged graph without metadata
        if (previousStatus.size() == 0)
            return false;
        for (final DataSourceMetadata metadata : previousStatus.values()) {
            // Existing data source not requested anymore?
            if (!requestedStatus.containsKey(metadata.id))
                return false;
            final DataSourceMetadata requestedMetadata = requestedStatus.get(metadata.id);
            // Existing data source out of date?
            if (requestedMetadata.exportVersion > metadata.exportVersion || requestedMetadata.version.compareTo(
                    metadata.version) > 0)
                return false;
            // Reexport forced?
            if (workspace.isDataSourceExportForced(metadata.id))
                return false;
        }
        return true;
    }

    private void mergeFromScratch(final Workspace workspace, final DataSource[] dataSources) throws MergerException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Creating new merged graph from scratch");
        try (Graph mergedGraph = new Graph(workspace.getFilePath(WorkspaceFileType.MERGED_PERSISTENT_GRAPH))) {
            for (final DataSource dataSource : dataSources)
                mergeDataSource(workspace, dataSource, mergedGraph);
            saveMergedGraph(workspace, mergedGraph);
            generateMetaGraphStatistics(mergedGraph, workspace);
        } catch (final Exception ex) {
            throw new MergerException(ex);
        }
    }

    private void mergeDataSource(final Workspace workspace, final DataSource dataSource,
                                 final Graph mergedGraph) throws MergerException {
        final Path intermediateGraphFilePath = dataSource.getFilePath(workspace, DataSourceFileType.PERSISTENT_GRAPH);
        if (!intermediateGraphFilePath.toFile().exists())
            throw new MergerException(
                    "Failed to merge data source " + dataSource.getId() + " because the exported graph is missing");
        try (Graph databaseToMerge = new Graph(intermediateGraphFilePath, true, true)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Merging data source " + dataSource.getId() + " [" + databaseToMerge.getNumberOfNodes() +
                            " nodes, " + databaseToMerge.getNumberOfEdges() + " edges]");
            mergedGraph.mergeDatabase(dataSource.getId(), databaseToMerge);
        } catch (GraphCacheException e) {
            throw new MergerException("Failed to merge data source " + dataSource.getId(), e);
        }
        addDataSourceMetadataNode(dataSource, mergedGraph);
    }

    private void addDataSourceMetadataNode(final DataSource dataSource, final Graph graph) {
        final Version version = dataSource.getMetadata().version;
        graph.addNode("metadata", "type", "datasource", "datasource_id", dataSource.getId(), "version",
                      version != null ? version.toString() : "", "export_version",
                      dataSource.getMetadata().exportVersion);
    }

    private void saveMergedGraph(final Workspace workspace, final Graph mergedGraph) {
        final Path outputGraphFilePath = workspace.getFilePath(WorkspaceFileType.MERGED_GRAPHML);
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
            LOGGER.info("Exporting merged meta graph image to " + metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, 2048, 2048);
        image.drawAndSaveImage(metaGraphImageFilePath);
        FileUtils.writeTextToUTF8File(metaGraphStatsFilePath, statistics);
        final MetaGraphDynamicVisWriter visWriter = new MetaGraphDynamicVisWriter(metaGraph);
        visWriter.write(metaGraphDynamicVisFilePath);
    }

    private void mergeFromPreviousStatus(final Workspace workspace, final DataSource[] dataSources,
                                         final Map<String, DataSourceMetadata> previousStatus) throws MergerException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Creating merged graph from previous status");
        try (Graph mergedGraph = new Graph(workspace.getFilePath(WorkspaceFileType.MERGED_PERSISTENT_GRAPH), true)) {
            for (final DataSource dataSource : dataSources)
                // Only add newly requested data sources
                if (!previousStatus.containsKey(dataSource.getId()))
                    mergeDataSource(workspace, dataSource, mergedGraph);
            saveMergedGraph(workspace, mergedGraph);
            generateMetaGraphStatistics(mergedGraph, workspace);
        } catch (final Exception ex) {
            throw new MergerException(ex);
        }
    }

    private static class DataSourceMetadata {
        public final String id;
        public final Version version;
        public final long exportVersion;

        private DataSourceMetadata(final String id, final Version version, final long exportVersion) {
            this.id = id;
            this.version = version;
            this.exportVersion = exportVersion;
        }
    }
}
