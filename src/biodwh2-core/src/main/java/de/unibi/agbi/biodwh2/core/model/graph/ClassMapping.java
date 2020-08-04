package de.unibi.agbi.biodwh2.core.model.graph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class ClassMapping {
    static class ClassMappingField {
        final Field field;
        final String propertyName;
        final String arrayDelimiter;

        ClassMappingField(final Field field, final String propertyName, final String arrayDelimiter) {
            this.field = field;
            this.propertyName = propertyName;
            this.arrayDelimiter = arrayDelimiter;
        }
    }

    final String label;
    final ClassMappingField[] fields;
    final ClassMappingField[] arrayFields;

    ClassMapping(final Class<?> type) {
        label = type.getAnnotation(NodeLabel.class).value();
        fields = loadClassMappingFields(type);
        arrayFields = loadClassMappingArrayFields(type);
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
        return new ClassMappingField(field, annotation.value(), null);
    }

    private ClassMappingField[] loadClassMappingArrayFields(final Class<?> type) {
        final List<ClassMappingField> fieldsList = new ArrayList<>();
        for (final Field field : type.getDeclaredFields())
            if (field.isAnnotationPresent(GraphArrayProperty.class))
                fieldsList.add(loadClassMappingArrayField(field));
        return fieldsList.toArray(new ClassMappingField[0]);
    }

    private ClassMappingField loadClassMappingArrayField(final Field field) {
        field.setAccessible(true);
        final GraphArrayProperty annotation = field.getAnnotation(GraphArrayProperty.class);
        return new ClassMappingField(field, annotation.value(), annotation.arrayDelimiter());
    }
}
