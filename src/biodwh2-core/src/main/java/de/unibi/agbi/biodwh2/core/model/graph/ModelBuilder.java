package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public abstract class ModelBuilder<S extends ModelBuilder<S>> extends HashMap<String, Object> {
    protected final Graph graph;
    protected String label;

    protected ModelBuilder(final Graph graph) {
        super();
        this.graph = graph;
    }

    public S withLabel(final String label) {
        this.label = label;
        return (S) this;
    }

    public <T> S withProperty(final String key, final T value) {
        put(key, value);
        return (S) this;
    }

    public S withPropertyIfNotNull(final String key, final String value) {
        if (value != null && !value.isEmpty())
            put(key, value);
        return (S) this;
    }

    public <T> S withPropertyIfNotNull(final String key, final T value) {
        if (value != null)
            put(key, value);
        return (S) this;
    }

    public S withModel(final Object obj) {
        final ClassMapping mapping = ClassMapping.get(obj);
        mapping.setModelBuilderProperties(this, obj);
        return (S) this;
    }

    public int getPropertyCount() {
        return size();
    }
}
