package de.unibi.agbi.biodwh2.core.io.mvstore;

public final class MVIndexDescription {
    private final String property;
    private final boolean isArrayProperty;
    private final MVStoreIndexType type;

    MVIndexDescription(final String property, final boolean isArrayProperty, final MVStoreIndexType type) {
        this.property = property;
        this.isArrayProperty = isArrayProperty;
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public boolean isArrayProperty() {
        return isArrayProperty;
    }

    public MVStoreIndexType getType() {
        return type;
    }
}
