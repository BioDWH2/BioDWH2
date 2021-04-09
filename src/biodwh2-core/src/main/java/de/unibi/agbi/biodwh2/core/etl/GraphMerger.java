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
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class GraphMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMerger.class);

    public final boolean merge(final Workspace workspace, final DataSource[] dataSources) throws MergerException {
        try (final Graph mergedGraph = new Graph(workspace.getFilePath(WorkspaceFileType.MERGED_PERSISTENT_GRAPH))) {
            for (final DataSource dataSource : dataSources)
                mergeDataSource(workspace, dataSource, mergedGraph);
            saveMergedGraph(workspace.getFilePath(WorkspaceFileType.MERGED_GRAPHML), mergedGraph);
            generateMetaGraphStatistics(mergedGraph, workspace);
        } catch (final Exception ex) {
            throw new MergerException(ex);
        }
        return true;
    }

    private void mergeDataSource(final Workspace workspace, final DataSource dataSource,
                                 final Graph mergedGraph) throws MergerException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Merging data source " + dataSource.getId());
        final Path intermediateGraphFilePath = dataSource.getFilePath(workspace, DataSourceFileType.PERSISTENT_GRAPH);
        try (Graph databaseToMerge = new Graph(intermediateGraphFilePath, true, true)) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Adding " + databaseToMerge.getNumberOfNodes() + " nodes and " +
                            databaseToMerge.getNumberOfEdges() + " edges");
            mergedGraph.mergeDatabase(dataSource.getId(), databaseToMerge);
            dataSource.getMetadata().mergeSuccessful = true;
        } catch (GraphCacheException e) {
            throw new MergerException("Failed to merge data source " + dataSource.getId(), e);
        }
    }

    private void saveMergedGraph(final Path outputFilePath, final Graph mergedGraph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Save merged graph to GraphML");
        final GraphMLGraphWriter graphMLWriter = new GraphMLGraphWriter();
        graphMLWriter.write(outputFilePath, mergedGraph);
    }

    private void generateMetaGraphStatistics(final Graph graph, final Workspace workspace) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Generating merged meta graph");
        final MetaGraph metaGraph = new MetaGraph(graph);
        final Path metaGraphImageFilePath = workspace.getFilePath(WorkspaceFileType.MERGED_META_GRAPH_IMAGE);
        final String statistics = new MetaGraphStatisticsWriter(metaGraph).write();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(statistics);
            LOGGER.info("Exporting merged meta graph image to " + metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, 2048, 2048);
        image.drawAndSaveImage(metaGraphImageFilePath);
        FileUtils.writeTextToUTF8File(workspace.getFilePath(WorkspaceFileType.MERGED_META_GRAPH_STATISTICS),
                                      statistics);
    }
}
