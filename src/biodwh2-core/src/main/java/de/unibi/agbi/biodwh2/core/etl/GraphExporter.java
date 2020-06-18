package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GraphExporter<D extends DataSource> {
    private static class ClassMapping {
        final String[] labels;
        final ClassMappingField[] fields;

        ClassMapping(final Class<?> type) {
            labels = type.getAnnotation(NodeLabels.class).value();
            List<ClassMappingField> fieldsList = new ArrayList<>();
            for (Field field : type.getDeclaredFields())
                if (field.isAnnotationPresent(GraphProperty.class)) {
                    field.setAccessible(true);
                    fieldsList.add(new ClassMappingField(field, field.getAnnotation(GraphProperty.class).value()));
                }
            fields = fieldsList.toArray(new ClassMappingField[0]);
        }
    }

    private static class ClassMappingField {
        final Field field;
        final String propertyName;

        ClassMappingField(final Field field, final String propertyName) {
            this.field = field;
            this.propertyName = propertyName;
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

    protected final Node createNodeFromModel(final Graph g, final Object obj) throws ExporterException {
        ClassMapping mapping = getClassMappingFromCache(obj.getClass());
        Node node = createNode(g, mapping.labels);
        setNodePropertiesFromClassMapping(node, mapping, obj);
        return node;
    }

    private ClassMapping getClassMappingFromCache(Class<?> type) {
        if (!classMappingsCache.containsKey(type))
            classMappingsCache.put(type, new ClassMapping(type));
        return classMappingsCache.get(type);
    }

    private void setNodePropertiesFromClassMapping(final Node node, final ClassMapping mapping,
                                                   final Object obj) throws ExporterException {
        try {
            for (ClassMappingField field : mapping.fields)
                node.setProperty(field.propertyName, field.field.get(obj));
        } catch (IllegalAccessException e) {
            throw new ExporterException(e);
        }
    }

    protected final Node createNode(final Graph g, final String... labels) {
        return g.addNode(labels);
    }

    protected final Node createNode(final Graph g, final List<String> labels) {
        return g.addNode(labels.toArray(new String[0]));
    }
}
