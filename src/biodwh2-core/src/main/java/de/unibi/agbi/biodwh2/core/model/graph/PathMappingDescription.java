package de.unibi.agbi.biodwh2.core.model.graph;

public class PathMappingDescription {
    public enum EdgeType {
        UNKNOWN,
        DUMMY,
        TARGETS,
        INDICATES,
        CONTRAINDICATES,
        INDUCES,
        INTERACTS
    }

    public EdgeType type = EdgeType.UNKNOWN;

    public PathMappingDescription() {
    }

    public PathMappingDescription(final EdgeType type) {
        this.type = type;
    }
}
