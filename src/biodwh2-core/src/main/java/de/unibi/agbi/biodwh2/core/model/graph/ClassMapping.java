package de.unibi.agbi.biodwh2.core.model.graph;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassMapping {
    public static class ClassMappingField {
        public final Field field;
        public final String propertyName;

        private ClassMappingField(final Field field, final String propertyName) {
            this.field = field;
            this.propertyName = propertyName;
        }
    }

    public final String[] labels;
    public final ClassMappingField[] fields;

    public ClassMapping(final Class<?> type) {
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
