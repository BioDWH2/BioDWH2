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
    private MVStore.TxCounter lock;

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

    void lock() {
        if (lock == null)
            lock = mvStore.registerVersionUsage();
    }

    void unlock() {
        if (lock != null)
            mvStore.deregisterVersionUsage(lock);
        lock = null;
    }

    V unsafeGet(Object key) {
        return mvMap.get(key);
    }

    @Override
    public V get(Object key) {
        lock();
        try {
            return clone(mvMap.get(key));
        } finally {
            unlock();
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

    void unsafePut(K key, V value) {
        mvMap.put(key, value);
    }

    @Override
    public V put(K key, V value) {
        lock();
        try {
            mvMap.put(key, value);
            return value;
        } finally {
            unlock();
        }
    }

    void unsafeRemove(Object key) {
        mvMap.remove(key);
    }

    @Override
    public V remove(Object key) {
        lock();
        try {
            return mvMap.remove(key);
        } finally {
            unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        lock();
        try {
            mvMap.putAll(m);
        } finally {
            unlock();
        }
    }

    @Override
    public void clear() {
        lock();
        try {
            mvMap.clear();
        } finally {
            unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        lock();
        try {
            return new HashSet<>(mvMap.keySet());
        } finally {
            unlock();
        }
    }

    Set<K> unsafeKeySet() {
        return mvMap.keySet();
    }

    @Override
    public Collection<V> values() {
        lock();
        try {
            return mvMap.values().stream().map(this::clone).collect(Collectors.toList());
        } finally {
            unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        lock();
        try {
            return mvMap.entrySet().stream().map(
                    e -> new AbstractMap.SimpleImmutableEntry<>(e.getKey(), clone(e.getValue()))).collect(
                    Collectors.toSet());
        } finally {
            unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        lock();
        try {
            return mvMap.putIfAbsent(key, value);
        } finally {
            unlock();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        lock();
        try {
            return mvMap.remove(key, value);
        } finally {
            unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        lock();
        try {
            // TODO: cloned object?
            return mvMap.replace(key, oldValue, newValue);
        } finally {
            unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        lock();
        try {
            return mvMap.replace(key, value);
        } finally {
            unlock();
        }
    }
}
