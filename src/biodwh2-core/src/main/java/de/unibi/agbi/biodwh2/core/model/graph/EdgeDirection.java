package de.unibi.agbi.biodwh2.core.model.graph;

public enum EdgeDirection {
    /**
     * (FromNode)-[Edge]-(ToNode)
     */
    BIDIRECTIONAL,
    /**
     * (FromNode)-[Edge]->(ToNode)
     */
    FORWARD,
    /**
     * (FromNode)<-[Edge]-(ToNode)
     */
    BACKWARD
}
