package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.h2.mvstore.MVStore;

import java.util.*;

public final class MVStoreDB implements AutoCloseable {
    private static final String COLLECTION_NAMES_KEY = "collection_names";

    private final boolean readOnly;
    private final MVStore store;
    private final MVMapWrapper<String, Object> metaMap;
    private final Map<String, MVStoreCollection<?>> collections;
    private final List<String> collectionNames;

    public MVStoreDB(final String filePath) {
        this(filePath, false);
    }

    public MVStoreDB(final String filePath, final boolean readOnly) {
        this.readOnly = readOnly;
        MVStore.Builder builder = new MVStore.Builder().compress().fileName(filePath);
        if (readOnly)
            builder = builder.readOnly();
        store = builder.open();
        metaMap = openMap("!meta");
        collections = new HashMap<>();
        collectionNames = new ArrayList<>();
        final String[] collectionNamesArray = (String[]) metaMap.get(COLLECTION_NAMES_KEY);
        if (collectionNamesArray != null)
            Collections.addAll(collectionNames, collectionNamesArray);
    }

    public <K, V> MVMapWrapper<K, V> openMap(final String name) {
        return new MVMapWrapper<>(store, store.openMap(name));
    }

    public <T extends MVStoreModel> MVStoreCollection<T> getCollection(final String name) {
        MVStoreCollection<?> collection = collections.get(name);
        if (collection == null) {
            collection = new MVStoreCollection<>(this, name, readOnly);
            collections.put(name, collection);
            if (!collectionNames.contains(name)) {
                collectionNames.add(name);
                metaMap.put(COLLECTION_NAMES_KEY, getCollectionNames());
            }
        }
        //noinspection unchecked
        return (MVStoreCollection<T>) collection;
    }

    public boolean removeCollection(final String name) {
        if (!readOnly) {
            collectionNames.remove(name);
            final MVStoreCollection<?> collection = collections.remove(name);
            if (collection != null)
                for (final String mapName : collection.getAllMapNames())
                    store.removeMap(mapName);
            metaMap.put(COLLECTION_NAMES_KEY, getCollectionNames());
            return true;
        }
        return false;
    }

    @Override
    public void close() {
        if (store != null && !store.isClosed())
            store.close();
    }

    public String[] getCollectionNames() {
        return collectionNames.toArray(new String[0]);
    }

    public MVStore.TxCounter getLock() {
        return store.registerVersionUsage();
    }

    public void releaseLock(final MVStore.TxCounter lock) {
        store.deregisterVersionUsage(lock);
    }
}
