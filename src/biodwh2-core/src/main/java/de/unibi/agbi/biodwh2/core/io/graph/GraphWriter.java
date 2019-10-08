package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;

import java.io.OutputStream;

public abstract class GraphWriter {
    public abstract boolean write(OutputStream stream, Graph graph);
}
