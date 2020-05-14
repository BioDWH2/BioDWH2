package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Node {
    private static final Logger logger = LoggerFactory.getLogger(Graph.class);

    private final Graph graph;
    private final long id;
    private final String[] labels;
    private final Map<String, Object> properties;
    private boolean modified;

    Node(Graph graph, long id, boolean modified, String... labels) {
        this.graph = graph;
        this.id = id;
        this.modified = modified;
        this.labels = labels;
        properties = new HashMap<>();
    }

    public long getId() {
        return id;
    }

    boolean isModified() {
        return modified;
    }

    public String[] getLabels() {
        return labels;
    }

    public Collection<String> getPropertyKeys() {
        return properties.keySet();
    }

    public Map<String, Class<?>> getPropertyKeyTypes() {
        Map<String, Class<?>> keyTypeMap = new HashMap<>();
        for (String key : properties.keySet()) {
            Class<?> propertyClass = properties.get(key) != null ? properties.get(key).getClass() : null;
            keyTypeMap.put(key, propertyClass);
        }
        return keyTypeMap;
    }

    public <T> T getProperty(String key) {
        //noinspection unchecked
        return (T) properties.get(key);
    }

    public void setProperty(final String key, final Object value) throws GraphCacheException {
        setProperty(key, value, true, true, true);
    }

    void setProperty(final String key, final Object value, final boolean persist, final boolean modified,
                     final boolean checkType) throws GraphCacheException {
        this.modified = modified;
        if (value != null) {
            if (checkType) {
                Class<?> valueType = value.getClass();
                if (valueType.isArray())
                    valueType = valueType.getComponentType();
                if (ClassUtils.isPrimitiveOrWrapper(valueType) || valueType == String.class)
                    properties.put(key, value);
                else {
                    logger.warn("Type '" + valueType.toString() + "' is not allowed as a node property. Using the " +
                                "toString representation for now '" + value.toString() + "'");
                    properties.put(key, value.toString());
                }
            } else
                properties.put(key, value);
            if (persist)
                graph.setNodeProperty(this, key, value);
        } else
            properties.put(key, null);
    }

    public boolean hasProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
