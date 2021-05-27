package de.unibi.agbi.biodwh2.core.io.obo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class OboStructure {
    private final Map<String, List<String>> keyValuePairs;

    OboStructure() {
        keyValuePairs = new HashMap<>();
    }

    void addKeyValuePair(final String key, final String value) {
        if (!keyValuePairs.containsKey(key))
            keyValuePairs.put(key, new ArrayList<>());
        keyValuePairs.get(key).add(value);
    }

    public boolean containsKey(final String key) {
        return keyValuePairs.containsKey(key);
    }

    public String[] get(final String key) {
        final List<String> value = keyValuePairs.get(key);
        return value == null ? null : value.toArray(new String[0]);
    }

    public String getFirst(final String key) {
        final List<String> values = keyValuePairs.get(key);
        return values == null || values.size() == 0 ? null : values.get(0);
    }

    protected Boolean getBooleanValue(final String key) {
        final String value = getFirst(key);
        return value == null ? null : "true".equalsIgnoreCase(value);
    }

    protected Boolean[] getBooleanValues(final String key) {
        final String[] values = get(key);
        if (values == null)
            return null;
        final Boolean[] result = new Boolean[values.length];
        for (int i = 0; i < values.length; i++)
            result[i] = values[i] == null ? null : "true".equalsIgnoreCase(values[i]);
        return result;
    }
}
