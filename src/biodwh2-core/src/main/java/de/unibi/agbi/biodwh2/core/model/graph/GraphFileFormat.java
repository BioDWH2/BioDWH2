package de.unibi.agbi.biodwh2.core.model.graph;

public enum GraphFileFormat {
    RDFTurtle("ttl"),
    GraphML("graphml");

    public final String extension;

    GraphFileFormat(String extension) {
        this.extension = extension;
    }
}
