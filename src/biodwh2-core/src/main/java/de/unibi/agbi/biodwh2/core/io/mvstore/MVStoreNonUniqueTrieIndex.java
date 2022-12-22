package de.unibi.agbi.biodwh2.core.io.mvstore;

import de.unibi.agbi.biodwh2.core.collections.ConcurrentDoublyLinkedList;
import de.unibi.agbi.biodwh2.core.collections.LongTrie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MVStoreNonUniqueTrieIndex extends MVStoreIndex {
    private final MVMapWrapper<Comparable<?>, LongTrie> map;
    private boolean isDelayed;
    private final ConcurrentMap<Comparable<?>, ConcurrentDoublyLinkedList<Long>> delayCache = new ConcurrentHashMap<>();

    public MVStoreNonUniqueTrieIndex(final MVStoreDB db, final String name, final String key,
                                     final boolean arrayIndex) {
        this(db, name, key, arrayIndex, false);
    }

    MVStoreNonUniqueTrieIndex(final MVStoreDB db, final String name, final String key, final boolean arrayIndex,
                              final boolean readOnly) {
        super(name, key, arrayIndex, readOnly);
        map = db.openMap(name);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Open MVStore non-unique trie index " + name + "[isArray=" + arrayIndex + "]");
    }

    @Override
    public MVStoreIndexType getType() {
        return MVStoreIndexType.NON_UNIQUE;
    }

    @Override
    public boolean contains(final Comparable<?> propertyValue) {
        return map.containsKey(propertyValue) || (isDelayed && delayCache.containsKey(propertyValue));
    }

    @Override
    public Collection<Comparable<?>> getIndexedValues() {
        return map.keySet();
    }

    @Override
    public void beginDelay() {
        isDelayed = true;
    }

    @Override
    public void endDelay() {
        if (isDelayed) {
            map.lock();
            try {
                for (final Comparable<?> indexKey : delayCache.keySet()) {
                    final ConcurrentDoublyLinkedList<Long> ids = delayCache.get(indexKey);
                    if (ids.size() == 0)
                        continue;
                    LongTrie trie = map.unsafeGet(indexKey);
                    if (trie == null)
                        trie = new LongTrie();
                    trie.addAll(delayCache.get(indexKey));
                    map.unsafePut(indexKey, trie);
                }
            } finally {
                map.unlock();
            }
            delayCache.clear();
        }
        isDelayed = false;
    }

    @Override
    public void put(final Object propertyValue, final long id) {
        if (arrayIndex)
            put((Comparable<?>[]) propertyValue, id);
        else
            put((Comparable<?>) propertyValue, id);
    }

    private void put(final Comparable<?> indexKey, final long id) {
        if (indexKey == null)
            return;
        if (isDelayed) {
            cacheDelayedId(indexKey, id);
            return;
        }
        map.lock();
        try {
            LongTrie trie = map.unsafeGet(indexKey);
            if (trie == null)
                trie = new LongTrie();
            trie.add(id);
            map.unsafePut(indexKey, trie);
        } finally {
            map.unlock();
        }
    }

    private void cacheDelayedId(final Comparable<?> indexKey, final long id) {
        ConcurrentDoublyLinkedList<Long> ids = delayCache.get(indexKey);
        if (ids == null) {
            ids = new ConcurrentDoublyLinkedList<>();
            delayCache.put(indexKey, ids);
        }
        ids.add(id);
    }

    private void put(final Comparable<?>[] indexKeys, final long id) {
        if (isDelayed) {
            for (final Comparable<?> indexKey : indexKeys)
                cacheDelayedId(indexKey, id);
            return;
        }
        map.lock();
        try {
            for (final Comparable<?> indexKey : indexKeys)
                if (indexKey != null) {
                    LongTrie trie = map.unsafeGet(indexKey);
                    if (trie == null)
                        trie = new LongTrie();
                    trie.add(id);
                    map.unsafePut(indexKey, trie);
                }
        } finally {
            map.unlock();
        }
    }

    @Override
    public Set<Long> find(final Comparable<?> indexKey) {
        final Set<Long> result = new HashSet<>();
        map.lock();
        try {
            final LongTrie trie = map.unsafeGet(indexKey);
            if (trie != null)
                result.addAll(trie);
        } finally {
            map.unlock();
        }
        if (isDelayed) {
            final ConcurrentDoublyLinkedList<Long> cache = delayCache.get(indexKey);
            if (cache != null)
                result.addAll(cache);
        }
        return result;
    }

    @Override
    public void remove(final Object propertyValue, final long id) {
        if (arrayIndex)
            remove((Comparable<?>[]) propertyValue, id);
        else
            remove((Comparable<?>) propertyValue, id);
    }

    private void remove(final Comparable<?> indexKey, final long id) {
        if (indexKey == null)
            return;
        if (isDelayed) {
            final ConcurrentDoublyLinkedList<Long> cache = delayCache.get(indexKey);
            if (cache != null)
                cache.remove(id);
        }
        map.lock();
        try {
            final LongTrie trie = map.unsafeGet(indexKey);
            if (trie != null) {
                trie.remove(id);
                map.unsafePut(indexKey, trie);
            }
        } finally {
            map.unlock();
        }
    }

    private void remove(final Comparable<?>[] indexKeys, final long id) {
        map.lock();
        try {
            for (final Comparable<?> indexKey : indexKeys)
                if (indexKey != null) {
                    if (isDelayed) {
                        final ConcurrentDoublyLinkedList<Long> cache = delayCache.get(indexKey);
                        if (cache != null)
                            cache.remove(id);
                    }
                    final LongTrie trie = map.unsafeGet(indexKey);
                    if (trie != null) {
                        trie.remove(id);
                        map.unsafePut(indexKey, trie);
                    }
                }
        } finally {
            map.unlock();
        }
    }
}
