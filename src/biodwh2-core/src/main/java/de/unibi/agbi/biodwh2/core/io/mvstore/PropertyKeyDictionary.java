package de.unibi.agbi.biodwh2.core.io.mvstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Append-only, order-stable mapping between the property keys of a collection and small integer ids. The compact model
 * encoding of {@link MVStoreModelValueType} stores these ids instead of repeating the key strings in every record, so
 * the dictionary must assign a stable id to each key and never reorder existing entries.
 * <p>
 * The dictionary is the in-memory form of the persisted {@code all_property_keys} array of a collection, which is why
 * loading from that array must preserve its order.
 */
final class PropertyKeyDictionary {
    private final List<String> keys = new ArrayList<>();
    private final Map<String, Integer> keyToId = new HashMap<>();

    /**
     * Load keys from a persisted array, preserving their order so that previously assigned ids stay stable. Keys not
     * yet known are appended; this never reorders or drops existing keys.
     */
    void load(final String[] persistedKeys) {
        if (persistedKeys == null)
            return;
        for (final String key : persistedKeys)
            if (key != null && !keyToId.containsKey(key)) {
                keyToId.put(key, keys.size());
                keys.add(key);
            }
    }

    /**
     * @return the id of the key, assigning and appending a new id if the key is not yet known
     */
    int idOf(final String key) {
        final Integer id = keyToId.get(key);
        if (id != null)
            return id;
        final int newId = keys.size();
        keys.add(key);
        keyToId.put(key, newId);
        return newId;
    }

    /**
     * @return the key for an id previously assigned by {@link #idOf(String)}
     */
    String keyOf(final int id) {
        if (id < 0 || id >= keys.size())
            throw new MVStoreIndexException("Unknown property key id " + id + " (dictionary size " + keys.size() + ')');
        return keys.get(id);
    }

    int size() {
        return keys.size();
    }
}
