package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.HashMap;
import java.util.Map;

public final class NodeBuilder {
    private final Graph graph;
    private final Map<String, Object> properties;
    private String label;

    NodeBuilder(final Graph graph) {
        this.graph = graph;
        properties = new HashMap<>();
    }

    public NodeBuilder withLabel(final String label) {
        this.label = label;
        return this;
    }

    public <T> NodeBuilder withProperty(final String key, final T value) {
        properties.put(key, value);
        return this;
    }

    public Node build() {
        return graph.addNode(label, properties);
    }
}
