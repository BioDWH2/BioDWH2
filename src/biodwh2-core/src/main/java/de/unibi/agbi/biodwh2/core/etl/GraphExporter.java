package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public abstract class GraphExporter<D extends DataSource> {
    static final String LABEL_PREFIX_SEPARATOR = "_";

    public GraphExporter(final D dataSource) {
        this.dataSource = dataSource;
    }

    protected final D dataSource;

    public final boolean export(final Workspace workspace) throws ExporterException {
        final Graph g = new Graph(dataSource.getGraphDatabaseFilePath(workspace));
        boolean exportSuccessful = exportGraph(workspace, g);
        if (exportSuccessful) {
            addDataSourcePrefixToGraph(g);
            exportSuccessful = trySaveGraphToFile(workspace, g);
        }
        g.dispose();
        return exportSuccessful;
    }

    protected abstract boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException;

    private void addDataSourcePrefixToGraph(final Graph g) {
        g.prefixAllLabels(dataSource.getId() + LABEL_PREFIX_SEPARATOR);
    }

    private boolean trySaveGraphToFile(final Workspace workspace, final Graph g) {
        return new GraphMLGraphWriter().write(workspace, dataSource, g);
    }

    protected final <T> Node createNodeFromModel(final Graph g, final T obj) {
        return g.addNodeFromModel(obj);
    }

    protected final Node createNode(final Graph g, final String label) {
        return g.addNode(label);
    }
}
