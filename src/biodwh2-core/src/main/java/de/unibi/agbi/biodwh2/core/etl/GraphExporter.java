package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GraphExporter<D extends DataSource> {
    private static class ClassMapping {
        String[] labels;
        Field[] fields;
        String[] fieldPropertyNames;

        ClassMapping(Class<?> type) {
            labels = type.getAnnotation(NodeLabels.class).value();
            fields = type.getDeclaredFields();
            fieldPropertyNames = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                fieldPropertyNames[i] = field.isAnnotationPresent(GraphProperty.class) ? field.getAnnotation(
                        GraphProperty.class).value() : field.getName();
            }
        }
    }

    private final Map<Class<?>, ClassMapping> classMappingsCache = new HashMap<>();

    public final boolean export(final Workspace workspace, final D dataSource) throws ExporterException {
        Graph g = new Graph(dataSource.getGraphDatabaseFilePath(workspace));
        boolean exportSuccessful = exportGraph(workspace, dataSource, g);
        g.synchronize(true);
        if (exportSuccessful) {
            addDataSourcePrefixToGraph(dataSource, g);
            exportSuccessful = trySaveGraphToFile(workspace, dataSource, g);
        }
        g.dispose();
        return exportSuccessful;
    }

    protected abstract boolean exportGraph(final Workspace workspace, final D dataSource,
                                           final Graph graph) throws ExporterException;

    private void addDataSourcePrefixToGraph(final DataSource dataSource, final Graph g) {
        g.prefixAllLabels(dataSource.getId());
    }

    private boolean trySaveGraphToFile(final Workspace workspace, final D dataSource, final Graph g) {
        return new GraphMLGraphWriter().write(workspace, dataSource, g);
    }

    protected final <T> Node createNodeFromModel(final Graph g, final T obj) throws ExporterException {
        Class<?> type = obj.getClass();
        if (!classMappingsCache.containsKey(type))
            classMappingsCache.put(type, new ClassMapping(type));
        ClassMapping mapping = classMappingsCache.get(type);
        Node node = createNode(g, mapping.labels);
        try {
            for (int i = 0; i < mapping.fields.length; i++)
                node.setProperty(mapping.fieldPropertyNames[i], mapping.fields[i].get(obj));
        } catch (IllegalAccessException e) {
            throw new ExporterException(e);
        }
        return node;
    }

    protected final Node createNode(final Graph g, final String... labels) {
        return g.addNode(labels);
    }

    protected final Node createNode(final Graph g, final List<String> labels) {
        return g.addNode(labels.toArray(new String[0]));
    }
}
