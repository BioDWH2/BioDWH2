package de.unibi.agbi.biodwh2.core.io.mvstore;

import java.util.*;

public final class MVStoreCollection<T extends MVStoreModel> implements Iterable<T> {
    private static final String INDEX_KEYS = "index_keys";
    private static final String INDEX_ARRAY_FLAGS = "index_array_flags";

    private final MVStoreDB db;
    private final String name;
    private final MVMapWrapper<Long, T> map;
    private final MVMapWrapper<String, Object> metaMap;
    private final Map<String, MVStoreIndex> indices;
    private boolean isDirty;

    MVStoreCollection(final MVStoreDB db, final String name) {
        this.db = db;
        this.name = name;
        map = db.openMap(name);
        metaMap = db.openMap(name + "!meta");
        indices = new HashMap<>();
        initIndices();
        isDirty = false;
    }

    private void initIndices() {
        final String[] indexKeys = (String[]) metaMap.get(INDEX_KEYS);
        final boolean[] indexArrayFlags = (boolean[]) metaMap.get(INDEX_ARRAY_FLAGS);
        if (indexKeys == null) {
            metaMap.put(INDEX_KEYS, new String[0]);
            metaMap.put(INDEX_ARRAY_FLAGS, new boolean[0]);
        } else {
            for (int i = 0; i < indexKeys.length; i++)
                getIndex(indexKeys[i], indexArrayFlags[i], true);
        }
    }

    public MVStoreIndex getIndex(final String key) {
        return getIndex(key, false, false);
    }

    public MVStoreIndex getIndex(final String key, final boolean arrayIndex) {
        return getIndex(key, arrayIndex, false);
    }

    private MVStoreIndex getIndex(final String key, final boolean arrayIndex, final boolean reopen) {
        MVStoreIndex index = indices.get(key);
        if (index == null) {
            index = new MVStoreIndex(db, name + "$" + key, key, arrayIndex);
            indices.put(key, index);
            if (!reopen) {
                addIndexMetadata(index);
                populateNewIndexIfDirty(index);
            }
        }
        return index;
    }

    private void addIndexMetadata(final MVStoreIndex index) {
        String[] indexKeys = (String[]) metaMap.get(INDEX_KEYS);
        indexKeys = indexKeys == null ? new String[1] : Arrays.copyOf(indexKeys, indexKeys.length + 1);
        indexKeys[indexKeys.length - 1] = index.getKey();
        boolean[] indexArrayFlags = (boolean[]) metaMap.get(INDEX_ARRAY_FLAGS);
        indexArrayFlags = indexArrayFlags == null ? new boolean[1] : Arrays.copyOf(indexArrayFlags,
                                                                                   indexArrayFlags.length + 1);
        indexArrayFlags[indexArrayFlags.length - 1] = index.isArrayIndex();
        metaMap.put(INDEX_KEYS, indexKeys);
        metaMap.put(INDEX_ARRAY_FLAGS, indexArrayFlags);
    }

    private void populateNewIndexIfDirty(final MVStoreIndex index) {
        if (isDirty)
            for (final T obj : map.values())
                index.put(obj.get(index.getKey()), obj.getId());
    }

    public void put(final T obj) {
        isDirty = true;
        for (final Map.Entry<String, Object> modifiedProperty : obj.getModifiedProperties().entrySet()) {
            final MVStoreIndex index = indices.get(modifiedProperty.getKey());
            if (index != null)
                index.remove(modifiedProperty.getValue(), obj.getId());
        }
        obj.resetModifiedProperties();
        map.put(obj.getIdValue(), obj);
        for (final MVStoreIndex index : indices.values())
            index.put(obj.get(index.getKey()), obj.getId());
    }

    public T get(final MVStoreId id) {
        return map.getOrDefault(id.getIdValue(), null);
    }

    public T get(final long id) {
        return map.getOrDefault(id, null);
    }

    public String getName() {
        return name;
    }

    public Iterable<T> find(final String propertyKey, final Comparable<?> propertyValue) {
        return find(new String[]{propertyKey}, new Comparable<?>[]{propertyValue});
    }

    public Iterable<T> find(final String propertyKey1, final Comparable<?> propertyValue1, final String propertyKey2,
                            final Comparable<?> propertyValue2) {
        return find(new String[]{propertyKey1, propertyKey2}, new Comparable<?>[]{propertyValue1, propertyValue2});
    }

    public Iterable<T> find(final String propertyKey1, final Comparable<?> propertyValue1, final String propertyKey2,
                            final Comparable<?> propertyValue2, final String propertyKey3,
                            final Comparable<?> propertyValue3) {
        return find(new String[]{propertyKey1, propertyKey2, propertyKey3},
                    new Comparable<?>[]{propertyValue1, propertyValue2, propertyValue3});
    }

    public Iterable<T> find(final String propertyKey1, final Comparable<?> propertyValue1, final String propertyKey2,
                            final Comparable<?> propertyValue2, final String propertyKey3,
                            final Comparable<?> propertyValue3, final String propertyKey4,
                            final Comparable<?> propertyValue4) {
        return find(new String[]{propertyKey1, propertyKey2, propertyKey3, propertyKey4},
                    new Comparable<?>[]{propertyValue1, propertyValue2, propertyValue3, propertyValue4});
    }

    public synchronized Iterable<T> find(final String[] propertyKeys, final Comparable<?>[] propertyValues) {
        Set<Long> ids = null;
        final boolean[] hasIndexFlags = new boolean[propertyKeys.length];
        for (int i = 0; i < propertyKeys.length; i++) {
            final MVStoreIndex index = indices.get(propertyKeys[i]);
            if (index != null) {
                hasIndexFlags[i] = true;
                if (ids == null)
                    ids = index.find(propertyValues[i]);
                else
                    ids.retainAll(index.find(propertyValues[i]));
            }
        }
        for (int i = 0; i < propertyKeys.length; i++) {
            if (!hasIndexFlags[i]) {
                final Comparable<?> searchValue = propertyValues[i];
                if (ids == null) {
                    ids = new HashSet<>();
                    for (final Long id : map.keySet()) {
                        final T obj = map.get(id);
                        final Comparable<?> value = obj.getProperty(propertyKeys[i]);
                        if (value != null && value.equals(searchValue))
                            ids.add(id);
                    }
                } else {
                    final Set<Long> matchedIds = new HashSet<>();
                    for (final Long id : ids) {
                        final T obj = map.get(id);
                        final Object value = obj.get(propertyKeys[i]);
                        if (value instanceof Comparable<?>) {
                            if (value.equals(searchValue))
                                matchedIds.add(id);
                        } else if (value instanceof Comparable<?>[]) {
                            final Comparable<?>[] valueArray = (Comparable<?>[]) value;
                            for (final Comparable<?> comparable : valueArray) {
                                if (comparable != null && comparable.equals(searchValue)) {
                                    matchedIds.add(id);
                                    break;
                                }
                            }
                        }
                    }
                    ids.retainAll(matchedIds);
                }
            }
        }
        final Set<Long> finalIds = ids != null ? ids : new HashSet<>();
        return () -> finalIds.stream().map(this::get).iterator();
    }

    @Override
    public Iterator<T> iterator() {
        final Set<Long> keys = new HashSet<>(map.keySet());
        return new Iterator<T>() {
            final Iterator<Long> entries = keys.iterator();

            @Override
            public boolean hasNext() {
                return entries.hasNext();
            }

            @Override
            public T next() {
                return get(entries.next());
            }
        };
    }

    public long size() {
        return map.sizeAsLong();
    }

    public MVStoreIndex[] getIndices() {
        return indices.values().toArray(new MVStoreIndex[0]);
    }

    public void remove(final T obj) {
        isDirty = true;
        map.remove(obj.getId().getIdValue());
        for (final MVStoreIndex index : indices.values())
            index.remove(obj.get(index.getKey()), obj.getId());
    }
}
