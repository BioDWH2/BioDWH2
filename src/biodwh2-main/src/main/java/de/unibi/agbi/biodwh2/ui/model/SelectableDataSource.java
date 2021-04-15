package de.unibi.agbi.biodwh2.ui.model;

public final class SelectableDataSource {
    private final String id;
    private final String name;
    private boolean active;

    public SelectableDataSource(final String id, final String name, final boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
