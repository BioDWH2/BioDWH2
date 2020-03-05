package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

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

    protected final <T> Node createNodeFromModel(final Graph g, final T obj) throws ExporterException {
        String[] labels = obj.getClass().getAnnotation(NodeLabels.class).value();
        Node node = createNode(g, labels);
        for (Field field : obj.getClass().getDeclaredFields())
            addPropertyToNode(obj, node, field);
        return node;
    }

    protected final Node createNode(final Graph g, final String... labels) {
        lastNodeId += 1;
        Node n = new Node(lastNodeId, labels);
        g.addNode(n);
        return n;
    }

    private <T> void addPropertyToNode(final T obj, final Node node, final Field field) throws ExporterException {
        field.setAccessible(true);
        if (field.isAnnotationPresent(GraphProperty.class)) {
            try {
                node.setProperty(field.getAnnotation(GraphProperty.class).value(), field.get(obj));
            } catch (IllegalAccessException e) {
                throw new ExporterException(e);
            }
        }
    }
}
