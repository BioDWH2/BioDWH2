package de.unibi.agbi.biodwh2.core.io.mvstore;

import de.unibi.agbi.biodwh2.core.collections.LongTrie;

import java.util.HashSet;
import java.util.Set;

public class MVStoreNonUniqueTrieIndex extends MVStoreIndex {
    private final MVMapWrapper<Comparable<?>, LongTrie> map;

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
        return map.containsKey(propertyValue);
    }

    @Override
    public void put(final Object propertyValue, final long id) {
        if (arrayIndex)
            put((Comparable<?>[]) propertyValue, id);
        else
            put((Comparable<?>) propertyValue, id);
    }

    private void put(final Comparable<?> indexKey, final long id) {
        if (indexKey != null) {
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
    }

    private void put(final Comparable<?>[] indexKeys, final long id) {
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
        map.lock();
        try {
            final LongTrie trie = map.unsafeGet(indexKey);
            return trie == null ? new HashSet<>() : new HashSet<>(trie);
        } finally {
            map.unlock();
        }
    }

    @Override
    public void remove(final Object propertyValue, final long id) {
        if (arrayIndex)
            remove((Comparable<?>[]) propertyValue, id);
        else
            remove((Comparable<?>) propertyValue, id);
    }

    private void remove(final Comparable<?> indexKey, final long id) {
        if (indexKey != null) {
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
    }

    private void remove(final Comparable<?>[] indexKeys, final long id) {
        map.lock();
        try {
            for (final Comparable<?> indexKey : indexKeys)
                if (indexKey != null) {
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
