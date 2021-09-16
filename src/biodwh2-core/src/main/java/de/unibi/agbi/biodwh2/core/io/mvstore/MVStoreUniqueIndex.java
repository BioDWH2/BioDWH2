package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public final class MVStoreUniqueIndex extends MVStoreIndex {
    private static final Logger LOGGER = LoggerFactory.getLogger(MVStoreUniqueIndex.class);

    private final MVMapWrapper<Comparable<?>, Long> map;

    public MVStoreUniqueIndex(final MVStoreDB db, final String name, final String key, final boolean arrayIndex) {
        this(db, name, key, arrayIndex, false);
    }

    public MVStoreUniqueIndex(final MVStoreDB db, final String name, final String key, final boolean arrayIndex,
                              final boolean readOnly) {
        super(name, key, false, readOnly);
        map = db.openMap(name);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Open MVStore unique index " + name + "[isArray=" + arrayIndex + "]");
    }

    @Override
    public MVStoreIndexType getType() {
        return MVStoreIndexType.UNIQUE;
    }

    @Override
    public boolean contains(final Comparable<?> propertyValue) {
        return map.containsKey(propertyValue);
    }

    @Override
    public void put(final Object propertyValue, final long id) {
        if (arrayIndex)
            put((Comparable<?>[]) propertyValue);
        else
            put((Comparable<?>) propertyValue, id);
    }

    private void put(final Comparable<?>[] propertyValue) {
        if (propertyValue != null)
            throw new MVStoreIndexException("Unique array indices are not yet supported!");
    }

    private void put(final Comparable<?> propertyValue, final long id) {
        if (propertyValue != null) {
            if (map.containsKey(propertyValue))
                throw new MVStoreIndexException(
                        "Unique index " + name + " already has a value for key '" + propertyValue + "'");
            map.put(propertyValue, id);
        }
    }

    @Override
    public Set<Long> find(final Comparable<?> propertyValue) {
        final Set<Long> ids = new HashSet<>();
        final Long id = map.get(propertyValue);
        if (id != null)
            ids.add(id);
        return ids;
    }

    @Override
    public void remove(final Object propertyValue, final long id) {
        if (arrayIndex)
            remove((Comparable<?>[]) propertyValue);
        else
            remove((Comparable<?>) propertyValue);
    }

    private void remove(final Comparable<?>[] indexKey) {
        if (indexKey != null)
            throw new MVStoreIndexException("Unique array indices are not yet supported!");
    }

    private void remove(final Comparable<?> indexKey) {
        if (indexKey != null)
            map.remove(indexKey);
    }
}
