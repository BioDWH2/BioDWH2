package de.unibi.agbi.biodwh2.core.model.graph;

public final class IndexDescription {
    private final Target target;
    private final String label;
    private final String property;
    private final boolean isArrayProperty;
    private final Type type;

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

    public enum Target {
        NODE,
        EDGE
    }

    public enum Type {
        UNIQUE,
        NON_UNIQUE
    }
}
