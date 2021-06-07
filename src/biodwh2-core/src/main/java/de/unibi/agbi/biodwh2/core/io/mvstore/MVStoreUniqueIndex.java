package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MVStoreUniqueIndex {
    private static final Logger LOGGER = LoggerFactory.getLogger(MVStoreUniqueIndex.class);

    private final String name;
    private final String key;
    private final MVMapWrapper<Comparable<?>, Long> map;

    public MVStoreUniqueIndex(final MVStoreDB db, final String name, final String key) {
        this.name = name;
        this.key = key;
        map = db.openMap(name);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Open MVStore unique index " + name);
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    private void put(final Comparable<?> indexKey, final long id) {
        if (indexKey != null) {
            if (map.containsKey(indexKey))
                throw new MVStoreIndexException("Unique index already has a value for key '" + indexKey + "'");
            map.put(indexKey, id);
        }
    }

    public Long find(final Comparable<?> indexKey) {
        return map.get(indexKey);
    }

    private void remove(final Comparable<?> indexKey) {
        if (indexKey != null)
            map.remove(indexKey);
    }
}
