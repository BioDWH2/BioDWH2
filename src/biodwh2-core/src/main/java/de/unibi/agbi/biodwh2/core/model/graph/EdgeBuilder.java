package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.HashMap;
import java.util.Map;

public final class EdgeBuilder {
    private final Graph graph;
    private final Map<String, Object> properties;
    private String label;
    private long fromId;
    private long toId;

    EdgeBuilder(final Graph graph) {
        this.graph = graph;
        properties = new HashMap<>();
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
        properties.put(key, value);
        return this;
    }

    public Edge build() {
        return graph.addEdge(fromId, toId, label, properties);
    }
}
