package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.util.*;

public final class MVStoreDB implements AutoCloseable {
    private final MVStore store;
    private final MVMap<String, Object> metaMap;
    private final Map<String, MVStoreCollection<?>> collections;
    private final List<String> collectionNames;

    public MVStoreDB(final String filePath) {
        store = new MVStore.Builder().compress().fileName(filePath).open();
        metaMap = openMap("!meta");
        collections = new HashMap<>();
        collectionNames = new ArrayList<>();
        final String[] collectionNamesArray = (String[]) metaMap.get("collection_names");
        if (collectionNamesArray != null)
            Collections.addAll(collectionNames, collectionNamesArray);
    }

    <K, V> MVMap<K, V> openMap(final String name) {
        return store.openMap(name);
    }

    public <T extends MVStoreModel> MVStoreCollection<T> getCollection(final String name) {
        MVStoreCollection<?> collection = collections.get(name);
        if (collection == null) {
            collection = new MVStoreCollection<>(this, name);
            collections.put(name, collection);
            collectionNames.add(name);
            metaMap.put("collection_names", getCollectionNames());
        }
        //noinspection unchecked
        return (MVStoreCollection<T>) collection;
    }

    public void close() {
        if (!store.isClosed())
            store.close();
    }

    public String[] getCollectionNames() {
        return collectionNames.toArray(new String[0]);
    }
}
