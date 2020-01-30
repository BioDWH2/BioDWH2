package de.unibi.agbi.biodwh2.core.model.graph;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private static final Logger logger = LoggerFactory.getLogger(Graph.class);

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
        if (value == null)
            properties.put(key, null);
        else {
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
        }
    }
}
