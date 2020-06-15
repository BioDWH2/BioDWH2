package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Edge implements PropertyContainer {
    private static final Logger logger = LoggerFactory.getLogger(Graph.class);

    private final Graph graph;
    private final long id;
    private final long fromId;
    private final long toId;
    private final String label;
    private final Map<String, Object> properties;

    Edge(Graph graph, long id, long fromId, long toId, String label) {
        this.graph = graph;
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.label = label;
        properties = new HashMap<>();
    }

    public long getId() {
        return id;
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

    public Map<String, Class<?>> getPropertyKeyTypes() {
        Map<String, Class<?>> keyTypeMap = new HashMap<>();
        for (String key : properties.keySet())
            keyTypeMap.put(key, properties.get(key).getClass());
        return keyTypeMap;
    }

    public <T> T getProperty(final String key) {
        //noinspection unchecked
        return (T) properties.get(key);
    }

    public void setProperty(final String key, final Object value) throws GraphCacheException {
        setProperty(key, value, true);
    }

    void setProperty(String key, Object value, boolean persist) throws GraphCacheException {
        if (value != null) {
            Class<?> valueType = value.getClass();
            if (valueType.isArray())
                valueType = valueType.getComponentType();
            if (ClassUtils.isPrimitiveOrWrapper(valueType) || valueType == String.class)
                properties.put(key, value);
            else {
                logger.warn("Type '" + valueType.toString() + "' is not allowed as an edge property. Using the " +
                            "toString representation for now '" + value.toString() + "'");
                properties.put(key, value.toString());
            }
            if (persist)
                graph.setEdgeProperty(this, key, value);
        }
    }
}
