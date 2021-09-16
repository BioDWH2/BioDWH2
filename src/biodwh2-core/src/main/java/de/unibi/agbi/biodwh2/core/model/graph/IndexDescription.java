package de.unibi.agbi.biodwh2.core.model.graph;

public final class IndexDescription {
    private final Target target;
    private final String label;
    private final String property;
    private final boolean isArrayProperty;
    private final Type type;

    public IndexDescription(final Target target, final String label, final String property) {
        this(target, label, property, false, Type.NON_UNIQUE);
    }

    public IndexDescription(final Target target, final String label, final String property,
                            final boolean isArrayProperty) {
        this(target, label, property, isArrayProperty, Type.NON_UNIQUE);
    }

    public IndexDescription(final Target target, final String label, final String property, final Type type) {
        this(target, label, property, false, type);
    }

    public IndexDescription(final Target target, final String label, final String property,
                            final boolean isArrayProperty, final Type type) {
        this.target = target;
        this.label = label;
        this.property = property;
        this.isArrayProperty = isArrayProperty;
        this.type = type;
    }

    public Target getTarget() {
        return target;
    }

    public String getLabel() {
        return label;
    }

    public String getProperty() {
        return property;
    }

    public boolean isArrayProperty() {
        return isArrayProperty;
    }

    public Type getType() {
        return type;
    }

    public static IndexDescription forNode(final String label, final String property) {
        return new IndexDescription(Target.NODE, label, property, false, Type.NON_UNIQUE);
    }

    public static IndexDescription forNode(final String label, final String property, final boolean isArrayProperty) {
        return new IndexDescription(Target.NODE, label, property, isArrayProperty, Type.NON_UNIQUE);
    }

    public static IndexDescription forNode(final String label, final String property, final Type type) {
        return new IndexDescription(Target.NODE, label, property, false, type);
    }

    public static IndexDescription forNode(final String label, final String property, final boolean isArrayProperty,
                                           final Type type) {
        return new IndexDescription(Target.NODE, label, property, isArrayProperty, type);
    }

    public static IndexDescription forEdge(final String label, final String property) {
        return new IndexDescription(Target.EDGE, label, property, false, Type.NON_UNIQUE);
    }

    public static IndexDescription forEdge(final String label, final String property, final boolean isArrayProperty) {
        return new IndexDescription(Target.EDGE, label, property, isArrayProperty, Type.NON_UNIQUE);
    }

    public static IndexDescription forEdge(final String label, final String property, final Type type) {
        return new IndexDescription(Target.EDGE, label, property, false, type);
    }

    public static IndexDescription forEdge(final String label, final String property, final boolean isArrayProperty,
                                           final Type type) {
        return new IndexDescription(Target.EDGE, label, property, isArrayProperty, type);
    }

    public enum Target {
        NODE,
        EDGE
    }

    public enum Type {
        UNIQUE,
        NON_UNIQUE
    }
}
