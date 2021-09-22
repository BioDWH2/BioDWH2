package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.HashMap;

public final class NodeBuilder extends HashMap<String, Object> {
    private static final long serialVersionUID = 9026156747952856158L;

    private final Graph graph;
    private String label;

    NodeBuilder(final Graph graph) {
        super();
        this.graph = graph;
    }

    public NodeBuilder withLabel(final String label) {
        this.label = label;
        return this;
    }

    public <T> NodeBuilder withProperty(final String key, final T value) {
        put(key, value);
        return this;
    }

    public <T> NodeBuilder withPropertyIfNotNull(final String key, final T value) {
        if (value != null)
            put(key, value);
        return this;
    }

    public NodeBuilder withModel(final Object obj) {
        final ClassMapping mapping = ClassMapping.get(obj);
        mapping.setNodeProperties(this, obj);
        return this;
    }

    public int getPropertyCount() {
        return size();
    }

    public Node build() {
        return graph.addNode(label, this);
    }
}
