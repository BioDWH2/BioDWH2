package de.unibi.agbi.biodwh2.core.io.mvstore;

import de.unibi.agbi.biodwh2.core.lang.Type;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;

import java.util.*;
import java.util.function.Consumer;

public final class MVStoreCollection<T extends MVStoreModel> implements Iterable<T> {
    private static final String INDEX_KEYS = "index_keys";
    private static final String INDEX_ARRAY_FLAGS = "index_array_flags";
    private static final String INDEX_TYPES = "index_types";
    private static final String ALL_PROPERTY_KEYS = "all_property_keys";
    private static final String ALL_PROPERTY_TYPES = "all_property_types";

    private final boolean readOnly;
    private final MVStoreDB db;
    private final String name;
    private final MVMapWrapper<Long, T> map;
    private final MVMapWrapper<String, Object> metaMap;
    private final Map<String, MVStoreIndex> indices;
    private final Map<String, Type> propertyKeyTypes;
    private boolean isDirty;

    MVStoreCollection(final MVStoreDB db, final String name, final boolean readOnly) {
        this.readOnly = readOnly;
        this.db = db;
        this.name = name;
        map = db.openMap(name);
        metaMap = db.openMap(name + "!meta");
        indices = new HashMap<>();
        propertyKeyTypes = new HashMap<>();
        initPropertyKeyTypes();
        isDirty = false;
        initIndices();
    }

    private void initIndices() {
        final String[] indexKeys = (String[]) metaMap.get(INDEX_KEYS);
        final boolean[] indexArrayFlags = (boolean[]) metaMap.get(INDEX_ARRAY_FLAGS);
        MVStoreIndexType[] indexTypes = (MVStoreIndexType[]) metaMap.get(INDEX_TYPES);
        if (indexKeys != null) {
            // legacy fill index types with non-unique as previously only those existed
            if (indexTypes == null) {
                indexTypes = new MVStoreIndexType[indexKeys.length];
                Arrays.fill(indexTypes, MVStoreIndexType.NON_UNIQUE);
            }
            for (int i = 0; i < indexKeys.length; i++)
                getIndex(indexKeys[i], indexArrayFlags[i], indexTypes[i], true);
        } else
            storeIndicesMetadata(new String[0], new boolean[0], new MVStoreIndexType[0]);
    }

    private void storeIndicesMetadata(final String[] keys, final boolean[] arrayFlags, final MVStoreIndexType[] types) {
        if (!readOnly) {
            metaMap.put(INDEX_KEYS, keys);
            metaMap.put(INDEX_ARRAY_FLAGS, arrayFlags);
            metaMap.put(INDEX_TYPES, types);
        }
    }

    private void initPropertyKeyTypes() {
        final String[] keys = (String[]) metaMap.get(ALL_PROPERTY_KEYS);
        final Type[] types = (Type[]) metaMap.get(ALL_PROPERTY_TYPES);
        if (keys == null || types == null)
            for (final T obj : map.values())
                updateAllPropertyKeys(obj);
        else
            for (int i = 0; i < keys.length; i++)
                propertyKeyTypes.put(keys[i], types[i]);
    }

    public MVStoreIndex getIndex(final String key) {
        return getIndex(key, false, MVStoreIndexType.NON_UNIQUE, false);
    }

    public MVStoreIndex getIndex(final String key, final boolean arrayIndex, final MVStoreIndexType type) {
        return getIndex(key, arrayIndex, type, false);
    }

    private MVStoreIndex getIndex(final String key, final boolean arrayIndex, final MVStoreIndexType type,
                                  final boolean reopen) {
        MVStoreIndex index = indices.get(key);
        if (index == null) {
            final String indexName = name + "$" + key;
            switch (type) {
                case UNIQUE:
                    index = new MVStoreUniqueIndex(db, indexName, key, arrayIndex, readOnly);
                    break;
                case NON_UNIQUE:
                default:
                    index = new MVStoreNonUniqueTrieIndex(db, indexName, key, arrayIndex, readOnly);
                    break;
            }
            indices.put(key, index);
            if (!reopen && !readOnly) {
                addIndexMetadata(index);
                populateNewIndexIfDirty(index);
            }
        }
        return index;
    }

    private void addIndexMetadata(final MVStoreIndex index) {
        String[] keys = (String[]) metaMap.get(INDEX_KEYS);
        keys = keys == null ? new String[1] : Arrays.copyOf(keys, keys.length + 1);
        keys[keys.length - 1] = index.getKey();
        boolean[] arrayFlags = (boolean[]) metaMap.get(INDEX_ARRAY_FLAGS);
        arrayFlags = arrayFlags == null ? new boolean[1] : Arrays.copyOf(arrayFlags, arrayFlags.length + 1);
        arrayFlags[arrayFlags.length - 1] = index.isArrayIndex();
        MVStoreIndexType[] types = (MVStoreIndexType[]) metaMap.get(INDEX_TYPES);
        types = types == null ? new MVStoreIndexType[1] : Arrays.copyOf(types, types.length + 1);
        types[types.length - 1] = index.getType();
        storeIndicesMetadata(keys, arrayFlags, types);
    }

    private void populateNewIndexIfDirty(final MVStoreIndex index) {
        if (isDirty)
            for (final T obj : map.values())
                index.put(obj.get(index.getKey()), obj.getId());
    }

    public MVIndexDescription[] getIndexDescriptions() {
        return indices.values().stream().filter(
                i -> !i.getKey().equals(Edge.FROM_ID_FIELD) && !i.getKey().equals(Edge.TO_ID_FIELD)).map(
                MVStoreIndex::getIndexDescription).toArray(MVIndexDescription[]::new);
    }

    public Map<String, Type> getPropertyKeyTypes() {
        return new HashMap<>(propertyKeyTypes);
    }

    public void put(final T obj) {
        isDirty = true;
        removeOldVersionFromIndices(map.get(obj.getId()));
        map.put(obj.getId(), obj);
        updateAllPropertyKeys(obj);
        for (final MVStoreIndex index : indices.values()) {
            final Object property = obj.get(index.getKey());
            if (property != null)
                index.put(property, obj.getId());
        }
    }

    private void removeOldVersionFromIndices(final T oldModel) {
        if (oldModel != null)
            for (final String key : oldModel.keySet()) {
                final MVStoreIndex index = indices.get(key);
                if (index != null) {
                    final Object property = oldModel.get(key);
                    if (property != null)
                        index.remove(property, oldModel.getId());
                }
            }
    }

    private void updateAllPropertyKeys(final T obj) {
        final int previousSize = propertyKeyTypes.size();
        boolean changed = false;
        for (final String key : obj.keySet()) {
            final Object value = obj.getProperty(key);
            if (value == null)
                continue;
            if (!propertyKeyTypes.containsKey(key)) {
                propertyKeyTypes.put(key, Type.fromObject(value));
                changed = true;
            } else if (propertyKeyTypes.get(key) != null) {
                final Type oldType = propertyKeyTypes.get(key);
                final Type newType = Type.fromObject(value);
                if (oldType.isList() && newType.getComponentType() != null) {
                    if (oldType.getComponentType() == null || newType.getComponentType().isAssignableFrom(
                            oldType.getComponentType())) {
                        propertyKeyTypes.put(key, newType);
                        changed = true;
                    }
                }
            }
        }
        if (changed || previousSize != propertyKeyTypes.size()) {
            final String[] keys = propertyKeyTypes.keySet().toArray(new String[0]);
            final Type[] types = new Type[keys.length];
            for (int i = 0; i < types.length; i++)
                types[i] = propertyKeyTypes.get(keys[i]);
            if (!readOnly) {
                metaMap.put(ALL_PROPERTY_KEYS, keys);
                metaMap.put(ALL_PROPERTY_TYPES, types);
            }
        }
    }

    public boolean contains(final long id) {
        return map.containsKey(id);
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
        for (final String propertyKey : propertyKeys)
            if (!propertyKeyTypes.containsKey(propertyKey))
                return new ArrayList<>();
        final boolean[] hasIndexFlags = new boolean[propertyKeys.length];
        Set<Long> ids = retainIndexedIds(propertyKeys, propertyValues, hasIndexFlags);
        if (isFindOnNonIndexedProperties(hasIndexFlags))
            ids = retainUnindexedIds(propertyKeys, propertyValues, hasIndexFlags, ids);
        final Set<Long> finalIds = ids != null ? ids : new HashSet<>();
        return () -> finalIds.stream().map(this::get).iterator();
    }

    private Set<Long> retainIndexedIds(final String[] propertyKeys, final Comparable<?>[] propertyValues,
                                       final boolean[] hasIndexFlags) {
        Set<Long> ids = null;
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
        return ids;
    }

    private boolean isFindOnNonIndexedProperties(final boolean[] hasIndexFlags) {
        for (final boolean hasIndexFlag : hasIndexFlags)
            if (!hasIndexFlag)
                return true;
        return false;
    }

    private Set<Long> retainUnindexedIds(final String[] propertyKeys, final Comparable<?>[] propertyValues,
                                         final boolean[] hasIndexFlags, Set<Long> ids) {
        if (ids == null) {
            ids = new HashSet<>();
            for (final Long id : map.keySet())
                if (modelMatchesCriteria(id, propertyKeys, propertyValues, hasIndexFlags))
                    ids.add(id);
        } else {
            final Set<Long> matchedIds = new HashSet<>();
            for (final Long id : ids)
                if (modelMatchesCriteria(id, propertyKeys, propertyValues, hasIndexFlags))
                    matchedIds.add(id);
            ids.retainAll(matchedIds);
        }
        return ids;
    }

    private boolean modelMatchesCriteria(final Long id, final String[] propertyKeys,
                                         final Comparable<?>[] propertyValues, final boolean[] hasIndexFlags) {
        final T obj = map.get(id);
        boolean matched = false;
        for (int i = 0; i < propertyKeys.length; i++) {
            if (hasIndexFlags[i])
                continue;
            final Comparable<?> searchValue = propertyValues[i];
            final Object value = obj.get(propertyKeys[i]);
            if (value instanceof Comparable<?>) {
                if (!propertyMatchesCriteria((Comparable<?>) value, searchValue))
                    return false;
            } else if (value instanceof Comparable<?>[]) {
                final Comparable<?>[] valueArray = (Comparable<?>[]) value;
                boolean matchedAnyInArray = false;
                for (final Comparable<?> comparable : valueArray) {
                    if (propertyMatchesCriteria(comparable, searchValue)) {
                        matchedAnyInArray = true;
                        break;
                    }
                }
                if (!matchedAnyInArray)
                    return false;
            } else
                return false;
            matched = true;
        }
        return matched;
    }

    private boolean propertyMatchesCriteria(final Comparable<?> a, final Comparable<?> b) {
        if (a == null || b == null)
            return false;
        if (a instanceof Long || a instanceof Integer || a instanceof Short || a instanceof Byte)
            if (b instanceof Long || b instanceof Integer || b instanceof Short || b instanceof Byte)
                return ((Number) a).longValue() == ((Number) b).longValue();
        return a.equals(b);
    }

    @Override
    public Iterator<T> iterator() {
        final Set<Long> keys = map.keySet();
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

    public Set<Long> keySet() {
        return map.keySet();
    }

    public MVStoreIndex[] getIndices() {
        return indices.values().toArray(new MVStoreIndex[0]);
    }

    public void remove(final T obj) {
        if (map.containsKey(obj.getId())) {
            removeOldVersionFromIndices(map.get(obj.getId()));
            map.remove(obj.getId());
            isDirty = true;
        }
    }

    String[] getAllMapNames() {
        final List<String> mapNames = new ArrayList<>();
        mapNames.add(map.name());
        mapNames.add(metaMap.name());
        for (final MVStoreIndex index : indices.values())
            mapNames.add(index.name);
        return mapNames.toArray(new String[0]);
    }

    public void beginIndicesDelay() {
        for (final MVStoreIndex index : indices.values())
            index.beginDelay();
    }

    public void endIndicesDelay() {
        for (final MVStoreIndex index : indices.values())
            index.endDelay();
    }

    public void fastUnsafeIteration(final Consumer<T> consumer) {
        final var cursor = map.getCursor();
        while (cursor.hasNext())
            consumer.accept(map.unsafeGet(cursor.next()));
    }
}
