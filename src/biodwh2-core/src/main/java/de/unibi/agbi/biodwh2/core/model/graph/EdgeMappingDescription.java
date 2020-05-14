package de.unibi.agbi.biodwh2.core.model.graph;

public class EdgeMappingDescription {
    public enum EdgeType {
        Unknown,
        Targets,
        Indicates,
        Contraindicates
    }

    public EdgeType type = EdgeType.Unknown;
}
