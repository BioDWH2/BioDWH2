package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.graphics.MetaGraphImage;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public abstract class GraphExporter<D extends DataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphExporter.class);
    public static final String ID_KEY = "id";

    protected final D dataSource;

    public GraphExporter(final D dataSource) {
        this.dataSource = dataSource;
    }

    public abstract long getExportVersion();

    public final boolean export(final Workspace workspace) throws ExporterException {
        final Graph g = new Graph(dataSource.getFilePath(workspace, DataSourceFileType.PERSISTENT_GRAPH));
        boolean exportSuccessful;
        try {
            exportSuccessful = exportGraph(workspace, g);
            if (exportSuccessful) {
                exportSuccessful = trySaveGraphToFile(workspace, g);
                if (exportSuccessful)
                    generateMetaGraphStatistics(workspace, g);
            }
        } finally {
            g.close();
        }
        return exportSuccessful;
    }

    protected abstract boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException;

    private boolean trySaveGraphToFile(final Workspace workspace, final Graph g) {
        final GraphMLGraphWriter writer = new GraphMLGraphWriter();
        if (workspace.getConfiguration().getSkipGraphMLExport()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Skipping '" + dataSource.getId() + "' GraphML export as per configuration");
            writer.removeOldExport(workspace, dataSource);
            return true;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Save '" + dataSource.getId() + "' data source graph to GraphML");
        return writer.write(workspace, dataSource, g);
    }

    private void generateMetaGraphStatistics(final Workspace workspace, final Graph g) {
        final Path metaGraphImageFilePath = dataSource.getFilePath(workspace, DataSourceFileType.META_GRAPH_IMAGE);
        final Path metaGraphStatsFilePath = dataSource.getFilePath(workspace, DataSourceFileType.META_GRAPH_STATISTICS);
        if (workspace.getConfiguration().getSkipMetaGraphGeneration()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Skipping '" + dataSource.getId() + "' meta graph generation as per configuration");
            FileUtils.safeDelete(metaGraphImageFilePath);
            FileUtils.safeDelete(metaGraphStatsFilePath);
            return;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Generating '" + dataSource.getId() + "' data source meta graph");
        final MetaGraph metaGraph = new MetaGraph(g);
        if (metaGraph.getNodeLabelCount() == 0 && metaGraph.getEdgeLabelCount() == 0) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Skipping meta graph image generation of empty meta graph");
            return;
        }
        final String statistics = new MetaGraphStatisticsWriter(metaGraph).write();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(statistics);
            LOGGER.info("Exporting meta graph image to " + metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, 1024, 1024);
        image.drawAndSaveImage(metaGraphImageFilePath);
        FileUtils.writeTextToUTF8File(metaGraphStatsFilePath, statistics);
    }

    protected final <T> void createNodesFromModels(final Graph g, final Iterable<T> models) {
        for (T obj : models)
            g.addNodeFromModel(obj);
    }
}
