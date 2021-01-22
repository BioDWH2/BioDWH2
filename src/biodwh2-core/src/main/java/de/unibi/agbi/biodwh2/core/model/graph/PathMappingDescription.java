package de.unibi.agbi.biodwh2.core.model.graph;

public class PathMappingDescription {
    @SuppressWarnings("unused")
    public enum EdgeType {
        UNKNOWN,
        DUMMY,
        TARGETS,
        INDICATES,
        CONTRAINDICATES,
        INDUCES,
        INTERACTS
    }

    private final String type;

    public PathMappingDescription(final EdgeType type) {
        this.type = type.name();
    }

    public PathMappingDescription(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
