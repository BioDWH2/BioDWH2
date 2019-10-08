package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Edge {
    private final long fromId;
    private final long toId;
    private final String label;
    private final Map<String, Object> properties;

    public Edge(Node from, Node to, String label) {
        fromId = from.getId();
        toId = to.getId();
        this.label = label;
        properties = new HashMap<>();
    }

    public long getFromId() {
        return fromId;
    }

    public long getToId() {
        return toId;
    }

    public String getLabel() {
        return label;
    }

    public Collection<String> getPropertyKeys() {
        return properties.keySet();
    }

    public <T> T getProperty(String key) {
        //noinspection unchecked
        return (T) properties.get(key);
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
}
