package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class ClassMapping {
    static class ClassMappingField {
        final Field field;
        final String propertyName;

        ClassMappingField(final Field field, final String propertyName) {
            this.field = field;
            this.propertyName = propertyName;
        }
    }

    static class ClassMappingArrayField extends ClassMappingField {
        final String arrayDelimiter;
        final String quotedArrayDelimiter;
        final boolean quotedArrayElements;

        ClassMappingArrayField(final Field field, final String propertyName, final String arrayDelimiter,
                               final boolean quotedArrayElements) {
            super(field, propertyName);
            this.arrayDelimiter = arrayDelimiter;
            quotedArrayDelimiter = "\"" + arrayDelimiter + "\"";
            this.quotedArrayElements = quotedArrayElements;
        }
    }

    static class ClassMappingBooleanField extends ClassMappingField {
        final String truthValue;

        ClassMappingBooleanField(final Field field, final String propertyName, final String truthValue) {
            super(field, propertyName);
            this.truthValue = truthValue;
        }
    }

    final String[] labels;
    final ClassMappingField[] fields;
    final ClassMappingArrayField[] arrayFields;
    final ClassMappingBooleanField[] booleanFields;

    ClassMapping(final Class<?> type) {
        labels = type.getAnnotation(NodeLabels.class).value();
        fields = loadClassMappingFields(type);
        arrayFields = loadClassMappingArrayFields(type);
        booleanFields = loadClassMappingBooleanFields(type);
    }

    private ClassMappingField[] loadClassMappingFields(final Class<?> type) {
        final List<ClassMappingField> fieldsList = new ArrayList<>();
        for (final Field field : type.getDeclaredFields())
            if (field.isAnnotationPresent(GraphProperty.class))
                fieldsList.add(loadClassMappingField(field));
        return fieldsList.toArray(new ClassMappingField[0]);
    }

    private ClassMappingField loadClassMappingField(final Field field) {
        field.setAccessible(true);
        final GraphProperty annotation = field.getAnnotation(GraphProperty.class);
        return new ClassMappingField(field, annotation.value());
    }

    private ClassMappingArrayField[] loadClassMappingArrayFields(final Class<?> type) {
        final List<ClassMappingArrayField> fieldsList = new ArrayList<>();
        for (final Field field : type.getDeclaredFields())
            if (field.isAnnotationPresent(GraphArrayProperty.class))
                fieldsList.add(loadClassMappingArrayField(field));
        return fieldsList.toArray(new ClassMappingArrayField[0]);
    }

    private ClassMappingArrayField loadClassMappingArrayField(final Field field) {
        field.setAccessible(true);
        final GraphArrayProperty annotation = field.getAnnotation(GraphArrayProperty.class);
        return new ClassMappingArrayField(field, annotation.value(), annotation.arrayDelimiter(),
                                          annotation.quotedArrayElements());
    }

    private ClassMappingBooleanField[] loadClassMappingBooleanFields(final Class<?> type) {
        final List<ClassMappingBooleanField> fieldsList = new ArrayList<>();
        for (final Field field : type.getDeclaredFields())
            if (field.isAnnotationPresent(GraphBooleanProperty.class))
                fieldsList.add(loadClassMappingBooleanField(field));
        return fieldsList.toArray(new ClassMappingBooleanField[0]);
    }

    private ClassMappingBooleanField loadClassMappingBooleanField(final Field field) {
        field.setAccessible(true);
        final GraphBooleanProperty annotation = field.getAnnotation(GraphBooleanProperty.class);
        return new ClassMappingBooleanField(field, annotation.value(), annotation.truthValue());
    }

    void setNodeProperties(final Node node, final Object obj) {
        try {
            setNodePropertiesFromFields(node, obj);
            setNodePropertiesFromArrayFields(node, obj);
            setNodePropertiesFromBooleanFields(node, obj);
        } catch (IllegalAccessException e) {
            throw new GraphCacheException(e);
        }
    }

    private void setNodePropertiesFromFields(final Node node, final Object obj) throws IllegalAccessException {
        for (final ClassMapping.ClassMappingField field : fields) {
            final Object value = field.field.get(obj);
            if (value != null)
                node.setProperty(field.propertyName, value);
        }
    }

    private void setNodePropertiesFromArrayFields(final Node node, final Object obj) throws IllegalAccessException {
        for (final ClassMapping.ClassMappingArrayField field : arrayFields) {
            final Object value = field.field.get(obj);
            if (value != null) {
                final String delimiter = field.quotedArrayElements ? field.quotedArrayDelimiter : field.arrayDelimiter;
                final String[] elements = StringUtils.splitByWholeSeparator(value.toString(), delimiter);
                if (field.quotedArrayElements && elements.length > 0) {
                    elements[0] = StringUtils.stripStart(elements[0], "\"");
                    elements[elements.length - 1] = StringUtils.stripEnd(elements[elements.length - 1], "\"");
                }
                node.setProperty(field.propertyName, elements);
            }
        }
    }

    private void setNodePropertiesFromBooleanFields(final Node node, final Object obj) throws IllegalAccessException {
        for (final ClassMapping.ClassMappingBooleanField field : booleanFields) {
            final Object value = field.field.get(obj);
            if (value != null)
                node.setProperty(field.propertyName, field.truthValue.equalsIgnoreCase(value.toString()));
        }
    }
}
