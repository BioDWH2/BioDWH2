package de.unibi.agbi.biodwh2.core.io.obo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OboEntry {
    private final String name;
    private final Map<String, List<String>> keyValuePairs;

    public OboEntry(final String name) {
        this.name = name;
        keyValuePairs = new HashMap<>();
    }

    void addKeyValuePair(String key, String value) {
        if (!keyValuePairs.containsKey(key))
            keyValuePairs.put(key, new ArrayList<>());
        keyValuePairs.get(key).add(value);
    }

    public String getName() {
        return name;
    }

    public boolean containsKey(final String key) {
        return keyValuePairs.containsKey(key);
    }

    public String[] get(final String key) {
        return keyValuePairs.getOrDefault(key, null).toArray(new String[0]);
    }

    public String getFirst(final String key) {
        return keyValuePairs.containsKey(key) ? keyValuePairs.get(key).get(0) : null;
    }
}
