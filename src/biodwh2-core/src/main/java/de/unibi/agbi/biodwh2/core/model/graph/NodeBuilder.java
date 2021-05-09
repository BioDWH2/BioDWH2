package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.HashMap;
import java.util.Map;

public final class NodeBuilder {
    private final Graph graph;
    private final Map<String, Object> properties;
    private String[] labels;

    NodeBuilder(final Graph graph) {
        this.graph = graph;
        properties = new HashMap<>();
    }

    public NodeBuilder withLabel(final String label) {
        return withLabels(label);
    }

    public NodeBuilder withLabels(final String... labels) {
        this.labels = labels;
        return this;
    }

    public <T> NodeBuilder withProperty(final String key, final T value) {
        properties.put(key, value);
        return this;
    }

    public <T> NodeBuilder withPropertyIfNotNull(final String key, final T value) {
        if (value != null)
            properties.put(key, value);
        return this;
    }

    public int getPropertyCount() {
        return properties.size();
    }

    public Node build() {
        return graph.addNode(labels, properties);
    }
}
