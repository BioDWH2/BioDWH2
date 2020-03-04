package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

import java.io.FileOutputStream;
import java.io.IOException;

public abstract class GraphExporter<D extends DataSource> {
    private long lastNodeId = 0;

    public final boolean export(final Workspace workspace, final D dataSource) throws ExporterException {
        lastNodeId = 0;
        Graph g = exportGraph(dataSource);
        if (g == null)
            return false;
        addDataSourcePrefixToGraphNodes(dataSource, g);
        return trySaveGraphToFile(workspace, dataSource, g);
    }

    protected abstract Graph exportGraph(D dataSource) throws ExporterException;

    private void addDataSourcePrefixToGraphNodes(final DataSource dataSource, final Graph g) {
        for (Node n : g.getNodes()) {
            final String[] labels = n.getLabels();
            for (int i = 0; i < labels.length; i++)
                if (!labels[i].contains(dataSource.getId()))
                    labels[i] = dataSource.getId() + "_" + labels[i];
        }
    }

    private boolean trySaveGraphToFile(final Workspace workspace, final D dataSource, final Graph g) {
        try {
            FileOutputStream outputStream = new FileOutputStream(
                    dataSource.getIntermediateGraphFilePath(workspace, GraphFileFormat.GraphML));
            return new GraphMLGraphWriter().write(outputStream, g);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected final Node createNode(final Graph g, final String... labels) {
        lastNodeId += 1;
        Node n = new Node(lastNodeId, labels);
        g.addNode(n);
        return n;
    }
}
