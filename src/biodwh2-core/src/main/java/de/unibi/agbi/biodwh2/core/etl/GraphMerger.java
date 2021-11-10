package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import de.unibi.agbi.biodwh2.core.graphics.MetaGraphImage;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.WorkspaceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.text.MetaGraphDynamicVisWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class GraphMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMerger.class);

    public final boolean merge(final Workspace workspace, final DataSource[] dataSources) throws MergerException {
        try (Graph mergedGraph = new Graph(workspace.getFilePath(WorkspaceFileType.MERGED_PERSISTENT_GRAPH))) {
            for (final DataSource dataSource : dataSources)
                mergeDataSource(workspace, dataSource, mergedGraph);
            saveMergedGraph(workspace, mergedGraph);
            generateMetaGraphStatistics(mergedGraph, workspace);
        } catch (final Exception ex) {
            throw new MergerException(ex);
        }
        return true;
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
        graph.addNode("metadata", "type", "datasource", "datasource_id", dataSource.getId(), "version",
                      dataSource.getMetadata().version.toString(), "export_version",
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
}
