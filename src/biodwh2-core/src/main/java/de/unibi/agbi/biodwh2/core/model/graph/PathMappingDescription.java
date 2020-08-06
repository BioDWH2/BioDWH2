package de.unibi.agbi.biodwh2.core.model.graph;

public class PathMappingDescription {
    public enum EdgeType {
        UNKNOWN,
        TARGETS,
        INDICATES,
        CONTRAINDICATES
    }

    public EdgeType type = EdgeType.UNKNOWN;
}
