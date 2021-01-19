package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Wrapper for the {@link MVStore} {@link MVMap} class to prevent concurrency issues with auto-commit. See:
 * https://github.com/h2database/h2database/issues/2590
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
final class MVMapWrapper<K, V> implements ConcurrentMap<K, V> {
    private final MVMap<K, V> mvMap;
    private final MVStore mvStore;

    MVMapWrapper(final MVStore mvStore, final MVMap<K, V> mvMap) {
        this.mvStore = mvStore;
        this.mvMap = mvMap;
    }

    @Override
    public int size() {
        return mvMap.size();
    }

    public long sizeAsLong() {
        return mvMap.sizeAsLong();
    }

    @Override
    public boolean isEmpty() {
        return mvMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return mvMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return mvMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            return clone(mvMap.get(key));
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    private V clone(final V value) {
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            new ObjectOutputStream(output).writeObject(value);
            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(output.toByteArray()));
            //noinspection unchecked
            return (V) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            mvMap.put(key, value);
            return value;
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public V remove(Object key) {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            return mvMap.remove(key);
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            mvMap.putAll(m);
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public void clear() {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            mvMap.clear();
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public Set<K> keySet() {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            return mvMap.keySet();
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public Collection<V> values() {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            return mvMap.values().stream().map(this::clone).collect(Collectors.toList());
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            return mvMap.entrySet().stream().map(
                    e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), clone(e.getValue()))).collect(
                    Collectors.toSet());
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            return mvMap.putIfAbsent(key, value);
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            return mvMap.remove(key, value);
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            // TODO: cloned object?
            return mvMap.replace(key, oldValue, newValue);
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }

    @Override
    public V replace(K key, V value) {
        MVStore.TxCounter txCounter = mvStore.registerVersionUsage();
        try {
            return mvMap.replace(key, value);
        } finally {
            mvStore.deregisterVersionUsage(txCounter);
        }
    }
}
