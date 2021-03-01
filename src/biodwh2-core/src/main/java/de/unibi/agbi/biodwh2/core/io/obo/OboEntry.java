package de.unibi.agbi.biodwh2.core.io.obo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OboEntry {
    private final String type;
    private final Map<String, List<String>> keyValuePairs;

    OboEntry(final String type) {
        this.type = type;
        keyValuePairs = new HashMap<>();
    }

    void addKeyValuePair(String key, String value) {
        if (!keyValuePairs.containsKey(key))
            keyValuePairs.put(key, new ArrayList<>());
        keyValuePairs.get(key).add(value);
    }

    public String getType() {
        return type;
    }

    public boolean containsKey(final String key) {
        return keyValuePairs.containsKey(key);
    }

    public String[] get(final String key) {
        return keyValuePairs.getOrDefault(key, null).toArray(new String[0]);
    }

    public String getFirst(final String key) {
        final List<String> values = keyValuePairs.get(key);
        return values == null || values.size() == 0 ? null : values.get(0);
    }

    protected Boolean getBooleanValue(final String key) {
        String value = getFirst(key);
        return value == null ? null : "true".equalsIgnoreCase(value);
    }
}
