package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.HashMap;

public final class EdgeBuilder extends HashMap<String, Object> {
    private static final long serialVersionUID = 8684453285018926870L;

    private final Graph graph;
    private String label;
    private long fromId;
    private long toId;

    EdgeBuilder(final Graph graph) {
        super();
        this.graph = graph;
    }

    public EdgeBuilder fromNode(Node node) {
        fromId = node.getId();
        return this;
    }

    public EdgeBuilder fromNode(long nodeId) {
        fromId = nodeId;
        return this;
    }

    public EdgeBuilder toNode(Node node) {
        toId = node.getId();
        return this;
    }

    public EdgeBuilder toNode(long nodeId) {
        toId = nodeId;
        return this;
    }

    public EdgeBuilder withLabel(final String label) {
        this.label = label;
        return this;
    }

    public <T> EdgeBuilder withProperty(final String key, final T value) {
        put(key, value);
        return this;
    }

    public <T> EdgeBuilder withPropertyIfNotNull(final String key, final T value) {
        if (value != null)
            put(key, value);
        return this;
    }

    public Edge build() {
        return graph.addEdge(fromId, toId, label, this);
    }
}
