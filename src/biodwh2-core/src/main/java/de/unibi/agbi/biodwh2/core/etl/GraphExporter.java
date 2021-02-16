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

    public final boolean export(final Workspace workspace) throws ExporterException {
        final Graph g = new Graph(dataSource.getFilePath(workspace, DataSourceFileType.PERSISTENT_GRAPH));
        boolean exportSuccessful;
        try {
            exportSuccessful = exportGraph(workspace, g) && trySaveGraphToFile(workspace, g);
            generateMetaGraphStatistics(workspace, g);
        } finally {
            g.close();
        }
        return exportSuccessful;
    }

    protected abstract boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException;

    private boolean trySaveGraphToFile(final Workspace workspace, final Graph g) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Save '" + dataSource.getId() + "' data source graph to GraphML");
        return new GraphMLGraphWriter().write(workspace, dataSource, g);
    }

    private void generateMetaGraphStatistics(final Workspace workspace, final Graph g) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Generating '" + dataSource.getId() + "' data source meta graph");
        final MetaGraph metaGraph = new MetaGraph(g);
        final Path metaGraphImageFilePath = dataSource.getFilePath(workspace, DataSourceFileType.META_GRAPH_IMAGE);
        final String statistics = new MetaGraphStatisticsWriter(metaGraph).write();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(statistics);
            LOGGER.info("Exporting meta graph image to " + metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, 1024, 1024);
        image.drawAndSaveImage(metaGraphImageFilePath);
        FileUtils.writeTextToUTF8File(dataSource.getFilePath(workspace, DataSourceFileType.META_GRAPH_STATISTICS),
                                      statistics);
    }

    protected final <T> void createNodesFromModels(final Graph g, final Iterable<T> models) {
        for (T obj : models)
            g.addNodeFromModel(obj);
    }

    protected final <T> Node createNodeFromModel(final Graph g, final T obj) {
        return g.addNodeFromModel(obj);
    }

    protected final Node createNode(final Graph g, final String label) {
        return g.addNode(label);
    }
}
