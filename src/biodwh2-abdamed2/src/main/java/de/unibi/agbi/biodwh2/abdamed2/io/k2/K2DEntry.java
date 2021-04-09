package de.unibi.agbi.biodwh2.abdamed2.io.k2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * K2 format "Delete" entry
 */
public class K2DEntry extends K2Entry {
    private Map<String, String> primaryKeys = new HashMap<>();

    K2DEntry(final EntryType type) {
        super(type);
    }

    @Override
    protected void parse(final String[] lines, final List<K2FEntry> fields) {
        for (int i = 0; i < lines.length; i++)
            primaryKeys.put(fields.get(i).getIdentifier(), lines[i]);
    }

    public Set<Map.Entry<String, String>> getPrimaryKeys() {
        return primaryKeys.entrySet();
    }
}
