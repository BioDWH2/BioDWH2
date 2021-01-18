package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.graphics.MetaGraphImage;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GraphExporter<D extends DataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphExporter.class);
    public static final String ID_KEY = "id";

    protected final D dataSource;

    public GraphExporter(final D dataSource) {
        this.dataSource = dataSource;
    }

    public final boolean export(final Workspace workspace) throws ExporterException {
        final Graph g = new Graph(dataSource.getGraphDatabaseFilePath(workspace));
        boolean exportSuccessful;
        try {
            exportSuccessful = exportGraph(workspace, g) && trySaveGraphToFile(workspace, g);
            saveMetaGraph(workspace, g);
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

    private void saveMetaGraph(final Workspace workspace, final Graph g) {
        final MetaGraph metaGraph = new MetaGraph(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Save '" + dataSource.getId() + "' data source meta graph image");
        final MetaGraphImage image = new MetaGraphImage(metaGraph, 1024, 1024);
        image.drawAndSaveImage(dataSource.getMetaGraphImageFilePath(workspace));
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
