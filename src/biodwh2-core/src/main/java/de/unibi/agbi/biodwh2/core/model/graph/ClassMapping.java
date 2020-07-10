package de.unibi.agbi.biodwh2.core.model.graph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class ClassMapping {
    static class ClassMappingField {
        final Field field;
        final String propertyName;
        final String arrayDelimiter;

        private ClassMappingField(final Field field, final String propertyName, final String arrayDelimiter) {
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
        List<ClassMappingField> fieldsList = new ArrayList<>();
        List<ClassMappingField> arrayFieldsList = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(GraphProperty.class)) {
                field.setAccessible(true);
                final GraphProperty annotation = field.getAnnotation(GraphProperty.class);
                fieldsList.add(new ClassMappingField(field, annotation.value(), null));
            }
            if (field.isAnnotationPresent(GraphArrayProperty.class)) {
                field.setAccessible(true);
                final GraphArrayProperty annotation = field.getAnnotation(GraphArrayProperty.class);
                arrayFieldsList.add(new ClassMappingField(field, annotation.value(), annotation.arrayDelimiter()));
            }
        }
        fields = fieldsList.toArray(new ClassMappingField[0]);
        arrayFields = arrayFieldsList.toArray(new ClassMappingField[0]);
    }
}
