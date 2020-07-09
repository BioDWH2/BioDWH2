package de.unibi.agbi.biodwh2.core.model.graph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class ClassMapping {
    static class ClassMappingField {
        final Field field;
        final String propertyName;

        private ClassMappingField(final Field field, final String propertyName) {
            this.field = field;
            this.propertyName = propertyName;
        }
    }

    final String label;
    final ClassMappingField[] fields;

    ClassMapping(final Class<?> type) {
        label = type.getAnnotation(NodeLabel.class).value();
        List<ClassMappingField> fieldsList = new ArrayList<>();
        for (Field field : type.getDeclaredFields())
            if (field.isAnnotationPresent(GraphProperty.class)) {
                field.setAccessible(true);
                fieldsList.add(new ClassMappingField(field, field.getAnnotation(GraphProperty.class).value()));
            }
        fields = fieldsList.toArray(new ClassMappingField[0]);
    }
}
