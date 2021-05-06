package de.unibi.agbi.biodwh2.abdamed2.io.k2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * K2 format "Insert" entry
 */
public class K2IEntry extends K2Entry {
    private Map<String, String> properties = new HashMap<>();

    K2IEntry(final EntryType type) {
        super(type);
    }

    @Override
    protected void parse(final String[] lines, final List<K2FEntry> fields) {
        for (int i = 0; i < lines.length; i++)
            properties.put(fields.get(i).getIdentifier(), lines[i]);
    }

    public Set<Map.Entry<String, String>> getEntries() {
        return properties.entrySet();
    }

    public boolean containsKey(final String key) {
        return properties.containsKey(key);
    }

    public String get(final String key) {
        return properties.get(key);
    }
}
