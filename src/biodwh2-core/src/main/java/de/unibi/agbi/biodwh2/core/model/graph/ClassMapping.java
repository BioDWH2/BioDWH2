package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreModel;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

final class ClassMapping {
    static class ClassMappingField {
        final Field field;
        final String propertyName;
        final boolean ignoreEmpty;
        final String emptyPlaceholder;

        ClassMappingField(final Field field, final String propertyName, final boolean ignoreEmpty,
                          final String emptyPlaceholder) {
            this.field = field;
            this.propertyName = propertyName;
            this.ignoreEmpty = ignoreEmpty;
            this.emptyPlaceholder = emptyPlaceholder != null ? emptyPlaceholder : "";
        }
    }

    static final class ClassMappingArrayField extends ClassMappingField {
        final String arrayDelimiter;
        final String quotedArrayDelimiter;
        final boolean quotedArrayElements;
        final String emptyPlaceholder;

        ClassMappingArrayField(final Field field, final String propertyName, final String arrayDelimiter,
                               final boolean quotedArrayElements, final String emptyPlaceholder) {
            super(field, propertyName, false, "");
            this.arrayDelimiter = arrayDelimiter;
            quotedArrayDelimiter = "\"" + arrayDelimiter + "\"";
            this.quotedArrayElements = quotedArrayElements;
            this.emptyPlaceholder = emptyPlaceholder != null ? emptyPlaceholder : "";
        }
    }

    static final class ClassMappingBooleanField extends ClassMappingField {
        final String truthValue;

        ClassMappingBooleanField(final Field field, final String propertyName, final String truthValue) {
            super(field, propertyName, false, "");
            this.truthValue = truthValue;
        }
    }

    static final class ClassMappingNumberField extends ClassMappingField {
        final GraphNumberProperty.Type type;

        ClassMappingNumberField(final Field field, final String propertyName, final boolean ignoreEmpty,
                                final String emptyPlaceholder, final GraphNumberProperty.Type type) {
            super(field, propertyName, ignoreEmpty, emptyPlaceholder);
            this.type = type;
        }
    }

    private static final Map<Class<?>, ClassMapping> cache = new HashMap<>();

    final String label;
    final ClassMappingField[] fields;
    final ClassMappingArrayField[] arrayFields;
    final ClassMappingBooleanField[] booleanFields;
    final ClassMappingNumberField[] numberFields;

    ClassMapping(final Class<?> type) {
        label = loadLabel(type);
        fields = loadClassMappingFields(type);
        arrayFields = loadClassMappingArrayFields(type);
        booleanFields = loadClassMappingBooleanFields(type);
        numberFields = loadClassMappingNumberFields(type);
    }

    private String loadLabel(final Class<?> type) {
        final GraphNodeLabel nodeLabel = type.getAnnotation(GraphNodeLabel.class);
        final GraphEdgeLabel edgeLabel = type.getAnnotation(GraphEdgeLabel.class);
        return nodeLabel != null ? nodeLabel.value() : (edgeLabel != null ? edgeLabel.value() : null);
    }

    private ClassMappingField[] loadClassMappingFields(final Class<?> type) {
        final List<ClassMappingField> fieldsList = new ArrayList<>();
        for (final Field field : getAllFieldsRecursive(new ArrayList<>(), type))
            if (field.isAnnotationPresent(GraphProperty.class))
                fieldsList.add(loadClassMappingField(field));
        return fieldsList.toArray(new ClassMappingField[0]);
    }

    private List<Field> getAllFieldsRecursive(final List<Field> fields, final Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null)
            getAllFieldsRecursive(fields, type.getSuperclass());
        return fields;
    }

    private ClassMappingField loadClassMappingField(final Field field) {
        field.setAccessible(true);
        final GraphProperty annotation = field.getAnnotation(GraphProperty.class);
        return new ClassMappingField(field, annotation.value(), annotation.ignoreEmpty(),
                                     annotation.emptyPlaceholder());
    }

    private ClassMappingArrayField[] loadClassMappingArrayFields(final Class<?> type) {
        final List<ClassMappingArrayField> fieldsList = new ArrayList<>();
        for (final Field field : getAllFieldsRecursive(new ArrayList<>(), type))
            if (field.isAnnotationPresent(GraphArrayProperty.class))
                fieldsList.add(loadClassMappingArrayField(field));
        return fieldsList.toArray(new ClassMappingArrayField[0]);
    }

    private ClassMappingArrayField loadClassMappingArrayField(final Field field) {
        field.setAccessible(true);
        final GraphArrayProperty annotation = field.getAnnotation(GraphArrayProperty.class);
        return new ClassMappingArrayField(field, annotation.value(), annotation.arrayDelimiter(),
                                          annotation.quotedArrayElements(), annotation.emptyPlaceholder());
    }

    private ClassMappingBooleanField[] loadClassMappingBooleanFields(final Class<?> type) {
        final List<ClassMappingBooleanField> fieldsList = new ArrayList<>();
        for (final Field field : getAllFieldsRecursive(new ArrayList<>(), type))
            if (field.isAnnotationPresent(GraphBooleanProperty.class))
                fieldsList.add(loadClassMappingBooleanField(field));
        return fieldsList.toArray(new ClassMappingBooleanField[0]);
    }

    private ClassMappingBooleanField loadClassMappingBooleanField(final Field field) {
        field.setAccessible(true);
        final GraphBooleanProperty annotation = field.getAnnotation(GraphBooleanProperty.class);
        return new ClassMappingBooleanField(field, annotation.value(), annotation.truthValue());
    }

    private ClassMappingNumberField[] loadClassMappingNumberFields(final Class<?> type) {
        final List<ClassMappingNumberField> fieldsList = new ArrayList<>();
        for (final Field field : getAllFieldsRecursive(new ArrayList<>(), type))
            if (field.isAnnotationPresent(GraphNumberProperty.class))
                fieldsList.add(loadClassMappingNumberField(field));
        return fieldsList.toArray(new ClassMappingNumberField[0]);
    }

    private ClassMappingNumberField loadClassMappingNumberField(final Field field) {
        field.setAccessible(true);
        final GraphNumberProperty annotation = field.getAnnotation(GraphNumberProperty.class);
        return new ClassMappingNumberField(field, annotation.value(), annotation.ignoreEmpty(),
                                           annotation.emptyPlaceholder(), annotation.type());
    }

    void setModelProperties(final MVStoreModel model, final Object obj) {
        try {
            setModelPropertiesFromFields(model, obj);
            setModelPropertiesFromArrayFields(model, obj);
            setModelPropertiesFromBooleanFields(model, obj);
            setModelPropertiesFromNumberFields(model, obj);
        } catch (IllegalAccessException e) {
            throw new GraphCacheException(e);
        }
    }

    private void setModelPropertiesFromFields(final MVStoreModel model,
                                              final Object obj) throws IllegalAccessException {
        for (final ClassMappingField field : fields)
            setModelPropertyFromField(model, obj, field);
    }

    private void setModelPropertyFromField(final MVStoreModel model, final Object obj,
                                           final ClassMappingField field) throws IllegalAccessException {
        final Object value = getFieldValue(obj, field);
        if (value != null)
            model.setProperty(field.propertyName, value);
    }

    private Object getFieldValue(final Object obj, final ClassMappingField field) throws IllegalAccessException {
        final Object value = field.field.get(obj);
        if (value != null) {
            if (value instanceof String) {
                final String stringValue = (String) value;
                if (field.emptyPlaceholder.length() > 0 && stringValue.equals(field.emptyPlaceholder))
                    return null;
                if (!field.ignoreEmpty || stringValue.length() > 0)
                    return value;
            } else
                return value;
        }
        return null;
    }

    private void setModelPropertiesFromArrayFields(final MVStoreModel model,
                                                   final Object obj) throws IllegalAccessException {
        for (final ClassMappingArrayField field : arrayFields)
            setModelPropertyFromArrayField(model, obj, field);
    }

    private void setModelPropertyFromArrayField(final MVStoreModel model, final Object obj,
                                                final ClassMappingArrayField field) throws IllegalAccessException {
        final String[] elements = getArrayFieldValue(obj, field);
        if (elements != null)
            model.setProperty(field.propertyName, elements);
    }

    private String[] getArrayFieldValue(final Object obj,
                                        final ClassMappingArrayField field) throws IllegalAccessException {
        final Object value = field.field.get(obj);
        if (value != null) {
            final String valueText = value.toString();
            if (field.emptyPlaceholder.length() > 0 && valueText.equals(field.emptyPlaceholder))
                return null;
            if (StringUtils.isEmpty(valueText))
                return new String[0];
            if (field.quotedArrayElements && valueText.startsWith("\"")) {
                final String[] elements = StringUtils.splitByWholeSeparator(valueText, field.quotedArrayDelimiter);
                elements[0] = StringUtils.stripStart(elements[0], "\"");
                elements[elements.length - 1] = StringUtils.stripEnd(elements[elements.length - 1], "\"");
                return elements;
            }
            return StringUtils.splitByWholeSeparator(valueText, field.arrayDelimiter);
        }
        return null;
    }

    private void setModelPropertiesFromBooleanFields(final MVStoreModel model,
                                                     final Object obj) throws IllegalAccessException {
        for (final ClassMappingBooleanField field : booleanFields)
            setModelPropertyFromBooleanField(model, obj, field);
    }

    private void setModelPropertyFromBooleanField(final MVStoreModel model, final Object obj,
                                                  final ClassMappingBooleanField field) throws IllegalAccessException {
        final Object value = field.field.get(obj);
        if (value != null)
            model.setProperty(field.propertyName, field.truthValue.equalsIgnoreCase(value.toString()));
    }

    private void setModelPropertiesFromNumberFields(final MVStoreModel model,
                                                    final Object obj) throws IllegalAccessException {
        for (final ClassMappingNumberField field : numberFields)
            setModelPropertiesFromNumberField(model, obj, field);
    }

    private void setModelPropertiesFromNumberField(final MVStoreModel model, final Object obj,
                                                   final ClassMappingNumberField field) throws IllegalAccessException {
        final Object value = getFieldValue(obj, field);
        if (value != null) {
            if (field.type == GraphNumberProperty.Type.Int)
                model.setProperty(field.propertyName, Integer.parseInt(value.toString()));
            else if (field.type == GraphNumberProperty.Type.Long)
                model.setProperty(field.propertyName, Long.parseLong(value.toString()));
        }
    }

    void setModelBuilderProperties(final ModelBuilder<?> builder, final Object obj) {
        try {
            setModelBuilderPropertiesFromFields(builder, obj);
            setModelBuilderPropertiesFromArrayFields(builder, obj);
            setModelBuilderPropertiesFromBooleanFields(builder, obj);
            setModelBuilderPropertiesFromNumberFields(builder, obj);
        } catch (IllegalAccessException e) {
            throw new GraphCacheException(e);
        }
    }

    private void setModelBuilderPropertiesFromFields(final ModelBuilder<?> builder,
                                                     final Object obj) throws IllegalAccessException {
        for (final ClassMappingField field : fields)
            setModelBuilderPropertyFromField(builder, obj, field);
    }

    private void setModelBuilderPropertyFromField(final ModelBuilder<?> builder, final Object obj,
                                                  final ClassMappingField field) throws IllegalAccessException {
        final Object value = getFieldValue(obj, field);
        if (value != null)
            builder.withProperty(field.propertyName, value);
    }

    private void setModelBuilderPropertiesFromArrayFields(final ModelBuilder<?> builder,
                                                          final Object obj) throws IllegalAccessException {
        for (final ClassMappingArrayField field : arrayFields)
            setModelBuilderPropertyFromArrayField(builder, obj, field);
    }

    private void setModelBuilderPropertyFromArrayField(final ModelBuilder<?> builder, final Object obj,
                                                       final ClassMappingArrayField field) throws IllegalAccessException {
        final String[] elements = getArrayFieldValue(obj, field);
        if (elements != null)
            builder.withProperty(field.propertyName, elements);
    }

    private void setModelBuilderPropertiesFromBooleanFields(final ModelBuilder<?> builder,
                                                            final Object obj) throws IllegalAccessException {
        for (final ClassMappingBooleanField field : booleanFields)
            setModelBuilderPropertyFromBooleanField(builder, obj, field);
    }

    private void setModelBuilderPropertyFromBooleanField(final ModelBuilder<?> builder, final Object obj,
                                                         final ClassMappingBooleanField field) throws IllegalAccessException {
        final Object value = field.field.get(obj);
        if (value != null)
            builder.withProperty(field.propertyName, field.truthValue.equalsIgnoreCase(value.toString()));
    }

    private void setModelBuilderPropertiesFromNumberFields(final ModelBuilder<?> builder,
                                                           final Object obj) throws IllegalAccessException {
        for (final ClassMappingNumberField field : numberFields)
            setModelBuilderPropertiesFromNumberField(builder, obj, field);
    }

    private void setModelBuilderPropertiesFromNumberField(final ModelBuilder<?> builder, final Object obj,
                                                          final ClassMappingNumberField field) throws IllegalAccessException {
        final Object value = getFieldValue(obj, field);
        if (value != null) {
            if (field.type == GraphNumberProperty.Type.Int)
                builder.withProperty(field.propertyName, Integer.parseInt(value.toString()));
            else if (field.type == GraphNumberProperty.Type.Long)
                builder.withProperty(field.propertyName, Long.parseLong(value.toString()));
        }
    }

    public static ClassMapping get(final Object obj) {
        return get(obj.getClass());
    }

    public static ClassMapping get(final Class<?> type) {
        if (!cache.containsKey(type))
            cache.put(type, new ClassMapping(type));
        return cache.get(type);
    }
}
