package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreModel;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

final class ClassMapping {
    static class ClassMappingField {
        final Field field;
        final String propertyName;
        final boolean ignoreEmpty;
        final String[] emptyPlaceholder;
        final ValueTransformation transformation;

        ClassMappingField(final Field field, final String propertyName, final boolean ignoreEmpty,
                          final String[] emptyPlaceholder, final ValueTransformation transformation) {
            this.field = field;
            this.propertyName = propertyName;
            this.ignoreEmpty = ignoreEmpty;
            this.emptyPlaceholder = emptyPlaceholder != null ? emptyPlaceholder : new String[0];
            this.transformation = transformation;
        }
    }

    static final class ClassMappingArrayField extends ClassMappingField {
        final Pattern arrayDelimiter;
        final Pattern quotedArrayDelimiter;
        final boolean quotedArrayElements;
        final String[] emptyPlaceholder;

        ClassMappingArrayField(final Field field, final String propertyName, final String[] arrayDelimiter,
                               final boolean quotedArrayElements, final String[] emptyPlaceholder) {
            super(field, propertyName, false, new String[0], ValueTransformation.NONE);
            final var arrayDelimiterBuilder = new StringBuilder();
            final var quotedArrayDelimiterBuilder = new StringBuilder();
            for (int i = 0; i < arrayDelimiter.length; i++) {
                if (i > 0) {
                    arrayDelimiterBuilder.append('|');
                    quotedArrayDelimiterBuilder.append('|');
                }
                arrayDelimiterBuilder.append(Pattern.quote(arrayDelimiter[i]));
                quotedArrayDelimiterBuilder.append(Pattern.quote("\"" + arrayDelimiter[i] + "\""));
            }
            this.arrayDelimiter = Pattern.compile(arrayDelimiterBuilder.toString());
            quotedArrayDelimiter = Pattern.compile(quotedArrayDelimiterBuilder.toString());
            this.quotedArrayElements = quotedArrayElements;
            this.emptyPlaceholder = emptyPlaceholder != null ? emptyPlaceholder : new String[0];
        }
    }

    static final class ClassMappingBooleanField extends ClassMappingField {
        final String truthValue;

        ClassMappingBooleanField(final Field field, final String propertyName, final String truthValue) {
            super(field, propertyName, false, new String[0], ValueTransformation.NONE);
            this.truthValue = truthValue;
        }
    }

    static final class ClassMappingNumberField extends ClassMappingField {
        final GraphNumberProperty.Type type;

        ClassMappingNumberField(final Field field, final String propertyName, final boolean ignoreEmpty,
                                final String[] emptyPlaceholder, final GraphNumberProperty.Type type) {
            super(field, propertyName, ignoreEmpty, emptyPlaceholder, ValueTransformation.NONE);
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
        return new ClassMappingField(field, annotation.value(), annotation.ignoreEmpty(), annotation.emptyPlaceholder(),
                                     annotation.transformation());
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

    @SuppressWarnings({"SuspiciousToArrayCall"})
    private Object getFieldValue(final Object obj, final ClassMappingField field) throws IllegalAccessException {
        final Object value = field.field.get(obj);
        if (value != null) {
            if (value instanceof String) {
                final String stringValue = (String) value;
                for (final String emptyPlaceholder : field.emptyPlaceholder)
                    if (!emptyPlaceholder.isEmpty() && stringValue.equals(emptyPlaceholder))
                        return null;
                if (!field.ignoreEmpty || !stringValue.isEmpty())
                    return value;
            } else if (value instanceof Collection) {
                if (field.transformation == ValueTransformation.COLLECTION_TO_STRING_ARRAY) {
                    final Collection<?> collection = (Collection<?>) value;
                    return collection.toArray(new String[0]);
                }
                if (field.transformation == ValueTransformation.COLLECTION_TO_ARRAY) {
                    final Collection<?> collection = (Collection<?>) value;
                    for (final Object element : collection) {
                        if (element instanceof String)
                            return collection.toArray(new String[0]);
                        if (element instanceof CharSequence)
                            return collection.toArray(new CharSequence[0]);
                        if (element instanceof Character)
                            return collection.toArray(new Character[0]);
                        if (element instanceof Byte)
                            return collection.toArray(new Byte[0]);
                        if (element instanceof Short)
                            return collection.toArray(new Short[0]);
                        if (element instanceof Integer)
                            return collection.toArray(new Integer[0]);
                        if (element instanceof Long)
                            return collection.toArray(new Long[0]);
                        if (element instanceof Double)
                            return collection.toArray(new Double[0]);
                        if (element instanceof Boolean)
                            return collection.toArray(new Boolean[0]);
                    }
                    return collection.toArray(new Object[0]);
                }
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
            for (final String emptyPlaceholder : field.emptyPlaceholder)
                if (!emptyPlaceholder.isEmpty() && valueText.equals(emptyPlaceholder))
                    return null;
            if (StringUtils.isEmpty(valueText))
                return new String[0];
            if (field.quotedArrayElements && valueText.startsWith("\"")) {
                final String[] elements = field.quotedArrayDelimiter.split(valueText);
                elements[0] = StringUtils.stripStart(elements[0], "\"");
                elements[elements.length - 1] = StringUtils.stripEnd(elements[elements.length - 1], "\"");
                return elements;
            }
            return field.arrayDelimiter.split(valueText);
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
