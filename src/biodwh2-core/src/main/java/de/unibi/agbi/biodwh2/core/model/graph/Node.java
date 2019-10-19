package de.unibi.agbi.biodwh2.core.model.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private final long id;
    private final String[] labels;
    private final Map<String, Object> properties;

    public Node(long id, String... labels) {
        this.id = id;
        this.labels = labels;
        properties = new HashMap<>();
    }

    public long getId() {
        return id;
    }

    public String[] getLabels() {
        return labels;
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
