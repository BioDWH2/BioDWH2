/*
 * Copyright 2004-2019 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 *
 * Modified to a minimal version for BioDWH2 graph databases
 */
package org.h2.mvstore;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A stored map.
 * <p>
 * All read and write operations can happen concurrently with all other operations, without risk of corruption.
 *
 * @param <K> the key class
 * @param <V> the value class
 */
public class MVMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    /**
     * The store.
     */
    public final MVStore store;

    /**
     * Reference to the current root page.
     */
    private final AtomicReference<RootReference> root;

    private final int id;
    private final long createVersion;
    private final DataType keyType;
    private final DataType valueType;

    private final Object lock = new Object();
    private volatile boolean notificationRequested;

    /**
     * Whether the map is closed. Volatile so we don't accidentally write to a closed map in multithreaded mode.
     */
    private volatile boolean closed;

    /**
     * This designates the "last stored" version for a store which was just open for the first time.
     */
    static final long INITIAL_VERSION = -1;

    protected MVMap(MVStore store, DataType keyType, DataType valueType, Map<String, Object> config) {
        this(store, keyType, valueType, DataUtils.readHexInt(config, "id", 0),
             DataUtils.readHexLong(config, "createVersion", 0), new AtomicReference<>());
        setInitialRoot(createEmptyLeaf(), store.getCurrentVersion());
    }

    // meta map constructor
    MVMap(MVStore store) {
        this(store, StringDataType.INSTANCE, StringDataType.INSTANCE, 0, 0, new AtomicReference<>());
        setInitialRoot(createEmptyLeaf(), store.getCurrentVersion());
    }

    private MVMap(MVStore store, DataType keyType, DataType valueType, int id, long createVersion,
                  AtomicReference<RootReference> root) {
        this.store = store;
        this.id = id;
        this.createVersion = createVersion;
        this.keyType = keyType;
        this.valueType = valueType;
        this.root = root;
    }

    /**
     * Get the metadata key for the root of the given map id.
     *
     * @param mapId the map id
     * @return the metadata key
     */
    static String getMapRootKey(int mapId) {
        return DataUtils.META_ROOT + Integer.toHexString(mapId);
    }

    /**
     * Get the metadata key for the given map id.
     *
     * @param mapId the map id
     * @return the metadata key
     */
    static String getMapKey(int mapId) {
        return DataUtils.META_MAP + Integer.toHexString(mapId);
    }

    /**
     * Add or replace a key-value pair.
     *
     * @param key   the key (may not be null)
     * @param value the value (may not be null)
     * @return the old value if the key existed, or null otherwise
     */
    @Override
    public V put(K key, V value) {
        if (value == null)
            throw new IllegalArgumentException("The value may not be null");
        return operate(key, value, DecisionMaker.PUT);
    }

    /**
     * Get the key at the given index.
     * <p>
     * This is a O(log(size)) operation.
     *
     * @param index the index
     * @return the key
     */
    public final K getKey(long index) {
        if (index < 0 || index >= sizeAsLong()) {
            return null;
        }
        Page p = getRootPage();
        long offset = 0;
        while (true) {
            if (p.isLeaf()) {
                if (index >= offset + p.getKeyCount()) {
                    return null;
                }
                @SuppressWarnings("unchecked") K key = (K) p.getKey((int) (index - offset));
                return key;
            }
            int i = 0, size = getChildPageCount(p);
            for (; i < size; i++) {
                long c = p.getCounts(i);
                if (index < c + offset) {
                    break;
                }
                offset += c;
            }
            if (i == size) {
                return null;
            }
            p = p.getChildPage(i);
        }
    }

    /**
     * Get the index of the given key in the map.
     * <p>
     * This is a O(log(size)) operation.
     * <p>
     * If the key was found, the returned value is the index in the key array. If not found, the returned value is
     * negative, where -1 means the provided key is smaller than any keys. See also Arrays.binarySearch.
     *
     * @param key the key
     * @return the index
     */
    public final long getKeyIndex(K key) {
        Page p = getRootPage();
        if (p.getTotalCount() == 0) {
            return -1;
        }
        long offset = 0;
        while (true) {
            int x = p.binarySearch(key);
            if (p.isLeaf()) {
                if (x < 0) {
                    offset = -offset;
                }
                return offset + x;
            }
            if (x++ < 0) {
                x = -x;
            }
            for (int i = 0; i < x; i++) {
                offset += p.getCounts(i);
            }
            p = p.getChildPage(x);
        }
    }

    /**
     * Get the value for the given key, or null if not found.
     *
     * @param key the key
     * @return the value, or null if not found
     * @throws ClassCastException if type of the specified key is not compatible with this map
     */
    @Override
    public final V get(Object key) {
        return get(getRootPage(), key);
    }

    /**
     * Get the value for the given key from a snapshot, or null if not found.
     *
     * @param p   the root of a snapshot
     * @param key the key
     * @return the value, or null if not found
     * @throws ClassCastException if type of the specified key is not compatible with this map
     */
    @SuppressWarnings("unchecked")
    public V get(Page p, Object key) {
        return (V) Page.get(p, key);
    }

    @Override
    public final boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * Remove all entries.
     */
    @Override
    public void clear() {
        Page emptyRootPage = createEmptyLeaf();
        int attempt = 0;
        while (true) {
            RootReference rootReference = flushAndGetRoot();
            if (rootReference.getTotalCount() == 0) {
                return;
            }
            boolean locked = rootReference.isLockedByCurrentThread();
            if (!locked) {
                if (attempt++ == 0) {
                    beforeWrite();
                } else if (attempt > 3 || rootReference.isLocked()) {
                    rootReference = lockRoot(rootReference, attempt);
                    locked = true;
                }
            }
            Page rootPage = rootReference.root;
            long version = rootReference.version;
            try {
                if (!locked) {
                    rootReference = rootReference.updateRootPage(emptyRootPage, attempt);
                    if (rootReference == null) {
                        continue;
                    }
                }
                store.registerUnsavedMemory(rootPage.removeAllRecursive(version));
                rootPage = emptyRootPage;
                return;
            } finally {
                if (locked) {
                    unlockRoot(rootPage);
                }
            }
        }
    }

    /**
     * Close the map. Accessing the data is still possible (to allow concurrent reads), but it is marked as closed.
     */
    final void close() {
        closed = true;
    }

    public final boolean isClosed() {
        return closed;
    }

    /**
     * Remove a key-value pair, if the key exists.
     *
     * @param key the key (may not be null)
     * @return the old value if the key existed, or null otherwise
     * @throws ClassCastException if type of the specified key is not compatible with this map
     */
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        return operate((K) key, null, DecisionMaker.REMOVE);
    }

    /**
     * Add a key-value pair if it does not yet exist.
     *
     * @param key   the key (may not be null)
     * @param value the new value
     * @return the old value if the key existed, or null otherwise
     */
    @Override
    public final V putIfAbsent(K key, V value) {
        return operate(key, value, DecisionMaker.IF_ABSENT);
    }

    /**
     * Remove a key-value pair if the value matches the stored one.
     *
     * @param key   the key (may not be null)
     * @param value the expected value
     * @return true if the item was removed
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object key, Object value) {
        EqualsDecisionMaker<V> decisionMaker = new EqualsDecisionMaker<>(valueType, (V) value);
        operate((K) key, null, decisionMaker);
        return decisionMaker.getDecision() != Decision.ABORT;
    }

    /**
     * Check whether the two values are equal.
     *
     * @param a        the first value
     * @param b        the second value
     * @param datatype to use for comparison
     * @return true if they are equal
     */
    static boolean areValuesEqual(DataType datatype, Object a, Object b) {
        return a == b || a != null && b != null && datatype.compare(a, b) == 0;
    }

    /**
     * Replace a value for an existing key, if the value matches.
     *
     * @param key      the key (may not be null)
     * @param oldValue the expected value
     * @param newValue the new value
     * @return true if the value was replaced
     */
    @Override
    public final boolean replace(K key, V oldValue, V newValue) {
        EqualsDecisionMaker<V> decisionMaker = new EqualsDecisionMaker<>(valueType, oldValue);
        V result = operate(key, newValue, decisionMaker);
        boolean res = decisionMaker.getDecision() != Decision.ABORT;
        assert !res || areValuesEqual(valueType, oldValue, result) : oldValue + " != " + result;
        return res;
    }

    private boolean rewrite(K key) {
        ContainsDecisionMaker<V> decisionMaker = new ContainsDecisionMaker<>();
        V result = operate(key, null, decisionMaker);
        boolean res = decisionMaker.getDecision() != Decision.ABORT;
        assert res == (result != null);
        return res;
    }

    /**
     * Replace a value for an existing key.
     *
     * @param key   the key (may not be null)
     * @param value the new value
     * @return the old value, if the value was replaced, or null
     */
    @Override
    public final V replace(K key, V value) {
        return operate(key, value, DecisionMaker.IF_PRESENT);
    }

    /**
     * Compare two keys.
     *
     * @param a the first key
     * @param b the second key
     * @return -1 if the first key is smaller, 1 if bigger, 0 if equal
     */
    final int compare(Object a, Object b) {
        return keyType.compare(a, b);
    }

    /**
     * Get the key type.
     *
     * @return the key type
     */
    public final DataType getKeyType() {
        return keyType;
    }

    /**
     * Get the value type.
     *
     * @return the value type
     */
    public final DataType getValueType() {
        return valueType;
    }

    /**
     * Read a page.
     *
     * @param pos the position of the page
     * @return the page
     */
    final Page readPage(long pos) {
        return store.readPage(this, pos);
    }

    /**
     * Set the position of the root page.
     *
     * @param rootPos the position, 0 for empty
     * @param version to set for this map
     */
    final void setRootPos(long rootPos, long version) {
        Page root = readOrCreateRootPage(rootPos);
        setInitialRoot(root, version);
        setWriteVersion(store.getCurrentVersion());
    }

    private Page readOrCreateRootPage(long rootPos) {
        return rootPos == 0 ? createEmptyLeaf() : readPage(rootPos);
    }

    /**
     * Iterate over a number of keys.
     *
     * @param from the first key to return
     * @return the iterator
     */
    public final Iterator<K> keyIterator(K from) {
        return new Cursor<K, V>(getRootPage(), from);
    }

    /**
     * Re-write any pages that belong to one of the chunks in the given set.
     *
     * @param set the set of chunk ids
     * @return number of pages actually re-written
     */
    final int rewrite(Set<Integer> set) {
        return rewrite(getRootPage(), set);
    }

    private int rewrite(Page p, Set<Integer> set) {
        if (p.isLeaf()) {
            long pos = p.getPos();
            int chunkId = DataUtils.getPageChunkId(pos);
            if (!set.contains(chunkId)) {
                return 0;
            }
            assert p.getKeyCount() > 0;
            return rewritePage(p) ? 1 : 0;
        }
        int writtenPageCount = 0;
        for (int i = 0; i < getChildPageCount(p); i++) {
            long childPos = p.getChildPagePos(i);
            if (childPos != 0 && DataUtils.getPageType(childPos) == DataUtils.PAGE_TYPE_LEAF) {
                // we would need to load the page, and it's a leaf:
                // only do that if it's within the set of chunks we are
                // interested in
                int chunkId = DataUtils.getPageChunkId(childPos);
                if (!set.contains(chunkId)) {
                    continue;
                }
            }
            writtenPageCount += rewrite(p.getChildPage(i), set);
        }
        if (writtenPageCount == 0) {
            long pos = p.getPos();
            int chunkId = DataUtils.getPageChunkId(pos);
            if (set.contains(chunkId)) {
                // an inner node page that is in one of the chunks,
                // but only points to chunks that are not in the set:
                // if no child was changed, we need to do that now
                // (this is not needed if anyway one of the children
                // was changed, as this would have updated this
                // page as well)
                while (!p.isLeaf()) {
                    p = p.getChildPage(0);
                }
                if (rewritePage(p)) {
                    writtenPageCount = 1;
                }
            }
        }
        return writtenPageCount;
    }

    private boolean rewritePage(Page p) {
        @SuppressWarnings("unchecked") K key = (K) p.getKey(0);
        if (!isClosed()) {
            return rewrite(key);
        }
        return true;
    }

    /**
     * Get a cursor to iterate over a number of keys and values.
     *
     * @param from the first key to return
     * @return the cursor
     */
    public final Cursor<K, V> cursor(K from) {
        return new Cursor<>(getRootPage(), from);
    }

    @Override
    public final Set<Map.Entry<K, V>> entrySet() {
        final Page root = this.getRootPage();
        return new AbstractSet<Entry<K, V>>() {

            @Override
            public Iterator<Entry<K, V>> iterator() {
                final Cursor<K, V> cursor = new Cursor<>(root, null);
                return new Iterator<Entry<K, V>>() {

                    @Override
                    public boolean hasNext() {
                        return cursor.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        K k = cursor.next();
                        return new SimpleImmutableEntry<>(k, cursor.getValue());
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Removal is not supported");
                    }
                };

            }

            @Override
            public int size() {
                return MVMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                return MVMap.this.containsKey(o);
            }

        };

    }

    @Override
    public Set<K> keySet() {
        final Page root = getRootPage();
        return new AbstractSet<K>() {

            @Override
            public Iterator<K> iterator() {
                return new Cursor<K, V>(root, null);
            }

            @Override
            public int size() {
                return MVMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                return MVMap.this.containsKey(o);
            }

        };
    }

    /**
     * Get the map name.
     *
     * @return the name
     */
    public final String getName() {
        return store.getMapName(id);
    }

    public final MVStore getStore() {
        return store;
    }

    protected final boolean isPersistent() {
        return store.getFileStore() != null;
    }

    /**
     * Get the map id. Please note the map id may be different after compacting a store.
     *
     * @return the map id
     */
    public final int getId() {
        return id;
    }

    /**
     * The current root page (may not be null).
     *
     * @return the root page
     */
    public final Page getRootPage() {
        return flushAndGetRoot().root;
    }

    public RootReference getRoot() {
        return root.get();
    }

    /**
     * Get the root reference, flushing any current append buffer.
     *
     * @return current root reference
     */
    public RootReference flushAndGetRoot() {
        return getRoot();
    }

    /**
     * Set the initial root.
     *
     * @param rootPage root page
     * @param version  initial version
     */
    final void setInitialRoot(Page rootPage, long version) {
        root.set(new RootReference(rootPage, version));
    }

    /**
     * Compare and set the root reference.
     *
     * @param expectedRootReference the old (expected)
     * @param updatedRootReference  the new
     * @return whether updating worked
     */
    final boolean compareAndSetRoot(RootReference expectedRootReference, RootReference updatedRootReference) {
        return root.compareAndSet(expectedRootReference, updatedRootReference);
    }

    /**
     * Forget those old versions that are no longer needed.
     *
     * @param rootReference to inspect
     */
    private void removeUnusedOldVersions(RootReference rootReference) {
        rootReference.removeUnusedOldVersions(store.getOldestVersionToKeep());
    }

    /**
     * This method is called before writing to the map. The default implementation checks whether writing is allowed,
     * and tries to detect concurrent modification.
     *
     * @throws UnsupportedOperationException if the map is read-only, or if another thread is concurrently writing
     */
    protected final void beforeWrite() {
        assert !getRoot().isLockedByCurrentThread() : getRoot();
        if (closed) {
            int id = getId();
            String mapName = store.getMapName(id);
            throw DataUtils.newIllegalStateException(DataUtils.ERROR_CLOSED, "Map {0}({1}) is closed. {2}", mapName, id,
                                                     store.getPanicException());
        }
        store.beforeWrite(this);
    }

    @Override
    public final int hashCode() {
        return id;
    }

    @Override
    public final boolean equals(Object o) {
        return this == o;
    }

    /**
     * Get the number of entries, as a integer. {@link Integer#MAX_VALUE} is returned if there are more than this
     * entries.
     *
     * @return the number of entries, as an integer
     * @see #sizeAsLong()
     */
    @Override
    public final int size() {
        long size = sizeAsLong();
        return size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size;
    }

    /**
     * Get the number of entries, as a long.
     *
     * @return the number of entries
     */
    public final long sizeAsLong() {
        return getRoot().getTotalCount();
    }

    @Override
    public boolean isEmpty() {
        return sizeAsLong() == 0;
    }

    public final long getCreateVersion() {
        return createVersion;
    }

    /**
     * Get version of the map, which is the version of the store, at the moment when map was modified last time.
     *
     * @return version
     */
    public final long getVersion() {
        return getRoot().getVersion();
    }

    /**
     * Does the root have changes since the specified version?
     *
     * @param version root version
     * @return true if has changes
     */
    final boolean hasChangesSince(long version) {
        return getRoot().hasChangesSince(version);
    }

    /**
     * Get the child page count for this page. This is to allow another map implementation to override the default, in
     * case the last child is not to be used.
     *
     * @param p the page
     * @return the number of direct children
     */
    protected int getChildPageCount(Page p) {
        return p.getRawChildPageCount();
    }

    /**
     * Get the map type. When opening an existing map, the map type must match.
     *
     * @return the map type
     */
    public String getType() {
        return null;
    }

    /**
     * Get the map metadata as a string.
     *
     * @param name the map name (or null)
     * @return the string
     */
    protected String asString(String name) {
        StringBuilder buff = new StringBuilder();
        if (name != null) {
            DataUtils.appendMap(buff, "name", name);
        }
        if (createVersion != 0) {
            DataUtils.appendMap(buff, "createVersion", createVersion);
        }
        String type = getType();
        if (type != null) {
            DataUtils.appendMap(buff, "type", type);
        }
        return buff.toString();
    }

    final RootReference setWriteVersion(long writeVersion) {
        int attempt = 0;
        while (true) {
            RootReference rootReference = flushAndGetRoot();
            if (rootReference.version >= writeVersion) {
                return rootReference;
            } else if (isClosed()) {
                // map was closed a while back and can not possibly be in use by now
                // it's time to remove it completely from the store (it was anonymous already)
                if (rootReference.getVersion() + 1 < store.getOldestVersionToKeep()) {
                    store.deregisterMapRoot(id);
                    return null;
                }
            }

            RootReference lockedRootReference = null;
            if (++attempt > 3 || rootReference.isLocked()) {
                lockedRootReference = lockRoot(rootReference, attempt);
                rootReference = flushAndGetRoot();
            }

            try {
                rootReference = rootReference.tryUnlockAndUpdateVersion(writeVersion, attempt);
                if (rootReference != null) {
                    lockedRootReference = null;
                    removeUnusedOldVersions(rootReference);
                    return rootReference;
                }
            } finally {
                if (lockedRootReference != null) {
                    unlockRoot();
                }
            }
        }
    }

    /**
     * Create empty leaf node page.
     *
     * @return new page
     */
    protected Page createEmptyLeaf() {
        return Page.createEmptyLeaf(this);
    }

    private static Page replacePage(CursorPos path, Page replacement, IntValueHolder unsavedMemoryHolder) {
        int unsavedMemory = replacement.isSaved() ? 0 : replacement.getMemory();
        while (path != null) {
            Page parent = path.page;
            // condition below should always be true, but older versions (up to 1.4.197)
            // may create single-childed (with no keys) internal nodes, which we skip here
            if (parent.getKeyCount() > 0) {
                Page child = replacement;
                replacement = parent.copy();
                replacement.setChild(path.index, child);
                unsavedMemory += replacement.getMemory();
            }
            path = path.parent;
        }
        unsavedMemoryHolder.value += unsavedMemory;
        return replacement;
    }

    /**
     * Appends entry to this map. this method is NOT thread safe and can not be used neither concurrently, nor in
     * combination with any method that updates this map. Non-updating method may be used concurrently, but latest
     * appended values are not guaranteed to be visible.
     *
     * @param key   should be higher in map's order than any existing key
     * @param value to be appended
     */
    public void append(K key, V value) {
        put(key, value);
    }

    @Override
    public final String toString() {
        return asString(null);
    }

    /**
     * A builder for this class.
     *
     * @param <K> the key type
     * @param <V> the value type
     */
    public static class Builder<K, V> {
        public MVMap<K, V> create(MVStore store, Map<String, Object> config) {
            return new MVMap<>(store, new ObjectDataType(), new ObjectDataType(), config);
        }
    }

    public enum Decision {
        ABORT,
        REMOVE,
        PUT,
        REPEAT
    }

    /**
     * Class DecisionMaker provides callback interface (and should become a such in Java 8) for MVMap.operate method. It
     * provides control logic to make a decision about how to proceed with update at the point in execution when proper
     * place and possible existing value for insert/update/delete key is found. Revised value for insert/update is also
     * provided based on original input value and value currently existing in the map.
     *
     * @param <V> value type of the map
     */
    public abstract static class DecisionMaker<V> {
        /**
         * Decision maker for put().
         */
        public static final DecisionMaker<Object> PUT = new DecisionMaker<Object>() {
            @Override
            public Decision decide(Object existingValue, Object providedValue) {
                return Decision.PUT;
            }

            @Override
            public String toString() {
                return "put";
            }
        };

        /**
         * Decision maker for remove().
         */
        public static final DecisionMaker<Object> REMOVE = new DecisionMaker<Object>() {
            @Override
            public Decision decide(Object existingValue, Object providedValue) {
                return Decision.REMOVE;
            }

            @Override
            public String toString() {
                return "remove";
            }
        };

        /**
         * Decision maker for putIfAbsent() key/value.
         */
        static final DecisionMaker<Object> IF_ABSENT = new DecisionMaker<Object>() {
            @Override
            public Decision decide(Object existingValue, Object providedValue) {
                return existingValue == null ? Decision.PUT : Decision.ABORT;
            }

            @Override
            public String toString() {
                return "if_absent";
            }
        };

        /**
         * Decision maker for replace().
         */
        static final DecisionMaker<Object> IF_PRESENT = new DecisionMaker<Object>() {
            @Override
            public Decision decide(Object existingValue, Object providedValue) {
                return existingValue != null ? Decision.PUT : Decision.ABORT;
            }

            @Override
            public String toString() {
                return "if_present";
            }
        };

        /**
         * Makes a decision about how to proceed with the update.
         *
         * @param existingValue value currently exists in the map
         * @param providedValue original input value
         * @return PUT if a new value need to replace existing one or new value to be inserted if there is none REMOVE
         * if existing value should be deleted ABORT if update operation should be aborted
         */
        public abstract Decision decide(V existingValue, V providedValue);

        /**
         * Provides revised value for insert/update based on original input value and value currently existing in the
         * map. This method is only invoked after call to decide(), if it returns PUT.
         *
         * @param existingValue value currently exists in the map
         * @param providedValue original input value
         * @param <T>           value type
         * @return value to be used by insert/update
         */
        public <T extends V> T selectValue(T existingValue, T providedValue) {
            return providedValue;
        }

        /**
         * Resets internal state (if any) of a this DecisionMaker to it's initial state. This method is invoked whenever
         * concurrent update failure is encountered, so we can re-start update process.
         */
        public void reset() {
        }
    }

    /**
     * Add, replace or remove a key-value pair.
     *
     * @param key           the key (may not be null)
     * @param value         new value, it may be null when removal is intended
     * @param decisionMaker command object to make choices during transaction.
     * @return previous value, if mapping for that key existed, or null otherwise
     */
    @SuppressWarnings("unchecked")
    public V operate(K key, V value, DecisionMaker<? super V> decisionMaker) {
        IntValueHolder unsavedMemoryHolder = new IntValueHolder();
        int attempt = 0;
        while (true) {
            RootReference rootReference = flushAndGetRoot();
            boolean locked = rootReference.isLockedByCurrentThread();
            if (!locked) {
                if (attempt++ == 0) {
                    beforeWrite();
                } else if (attempt > 3 || rootReference.isLocked()) {
                    rootReference = lockRoot(rootReference, attempt);
                    locked = true;
                }
            }
            Page rootPage = rootReference.root;
            long version = rootReference.version;
            CursorPos tip;
            V result;
            unsavedMemoryHolder.value = 0;
            try {
                CursorPos pos = CursorPos.traverseDown(rootPage, key);
                if (!locked && rootReference != getRoot()) {
                    continue;
                }
                Page p = pos.page;
                int index = pos.index;
                tip = pos;
                pos = pos.parent;
                result = index < 0 ? null : (V) p.getValue(index);
                Decision decision = decisionMaker.decide(result, value);

                switch (decision) {
                    case REPEAT:
                        decisionMaker.reset();
                        continue;
                    case ABORT:
                        if (!locked && rootReference != getRoot()) {
                            decisionMaker.reset();
                            continue;
                        }
                        return result;
                    case REMOVE: {
                        if (index < 0) {
                            if (!locked && rootReference != getRoot()) {
                                decisionMaker.reset();
                                continue;
                            }
                            return null;
                        }

                        if (p.getTotalCount() == 1 && pos != null) {
                            int keyCount;
                            do {
                                p = pos.page;
                                index = pos.index;
                                pos = pos.parent;
                                keyCount = p.getKeyCount();
                                // condition below should always be false, but older
                                // versions (up to 1.4.197) may create
                                // single-childed (with no keys) internal nodes,
                                // which we skip here
                            } while (keyCount == 0 && pos != null);

                            if (keyCount <= 1) {
                                if (keyCount == 1) {
                                    assert index <= 1;
                                    p = p.getChildPage(1 - index);
                                } else {
                                    // if root happens to be such single-childed
                                    // (with no keys) internal node, then just
                                    // replace it with empty leaf
                                    p = Page.createEmptyLeaf(this);
                                }
                                break;
                            }
                        }
                        p = p.copy();
                        p.remove(index);
                        break;
                    }
                    case PUT: {
                        value = decisionMaker.selectValue(result, value);
                        p = p.copy();
                        if (index < 0) {
                            p.insertLeaf(-index - 1, key, value);
                            int keyCount;
                            while ((keyCount = p.getKeyCount()) > MVStore.KEYS_PER_PAGE ||
                                   p.getMemory() > store.getMaxPageSize() && keyCount > (p.isLeaf() ? 1 : 2)) {
                                long totalCount = p.getTotalCount();
                                int at = keyCount >> 1;
                                Object k = p.getKey(at);
                                Page split = p.split(at);
                                unsavedMemoryHolder.value += p.getMemory() + split.getMemory();
                                if (pos == null) {
                                    Object[] keys = {k};
                                    Page.PageReference[] children = {
                                            new Page.PageReference(p), new Page.PageReference(split)
                                    };
                                    p = Page.createNode(this, keys, children, totalCount, 0);
                                    break;
                                }
                                Page c = p;
                                p = pos.page;
                                index = pos.index;
                                pos = pos.parent;
                                p = p.copy();
                                p.setChild(index, split);
                                p.insertNode(index, k, c);
                            }
                        } else {
                            p.setValue(index, value);
                        }
                        break;
                    }
                }
                rootPage = replacePage(pos, p, unsavedMemoryHolder);
                if (!locked) {
                    rootReference = rootReference.updateRootPage(rootPage, attempt);
                    if (rootReference == null) {
                        decisionMaker.reset();
                        continue;
                    }
                }
                store.registerUnsavedMemory(unsavedMemoryHolder.value + tip.processRemovalInfo(version));
                return result;
            } finally {
                if (locked) {
                    unlockRoot(rootPage);
                }
            }
        }
    }

    private RootReference lockRoot(RootReference rootReference, int attempt) {
        while (true) {
            RootReference lockedRootReference = tryLock(rootReference, attempt++);
            if (lockedRootReference != null) {
                return lockedRootReference;
            }
            rootReference = getRoot();
        }
    }

    /**
     * Try to lock the root.
     *
     * @param rootReference the old root reference
     * @param attempt       the number of attempts so far
     * @return the new root reference
     */
    protected RootReference tryLock(RootReference rootReference, int attempt) {
        RootReference lockedRootReference = rootReference.tryLock(attempt);
        if (lockedRootReference != null) {
            return lockedRootReference;
        }

        RootReference oldRootReference = rootReference.previous;
        int contention = 1;
        if (oldRootReference != null) {
            long updateAttemptCounter = rootReference.updateAttemptCounter - oldRootReference.updateAttemptCounter;
            assert updateAttemptCounter >= 0 : updateAttemptCounter;
            long updateCounter = rootReference.updateCounter - oldRootReference.updateCounter;
            assert updateCounter >= 0 : updateCounter;
            assert updateAttemptCounter >= updateCounter : updateAttemptCounter + " >= " + updateCounter;
            contention += (int) ((updateAttemptCounter + 1) / (updateCounter + 1));
        }

        if (attempt > 4) {
            if (attempt <= 12) {
                Thread.yield();
            } else if (attempt <= 70 - 2 * contention) {
                try {
                    Thread.sleep(contention);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                synchronized (lock) {
                    notificationRequested = true;
                    try {
                        lock.wait(5);
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * Unlock the root page, the new root being null.
     */
    private void unlockRoot() {
        unlockRoot(null);
    }

    private void unlockRoot(Page newRootPage) {
        RootReference updatedRootReference;
        do {
            RootReference rootReference = getRoot();
            assert rootReference.isLockedByCurrentThread();
            updatedRootReference = rootReference.updatePageAndLockedStatus(
                    newRootPage == null ? rootReference.root : newRootPage, rootReference.getAppendCounter());
        } while (updatedRootReference == null);
        notifyWaiters();
    }

    private void notifyWaiters() {
        if (notificationRequested) {
            synchronized (lock) {
                notificationRequested = false;
                lock.notify();
            }
        }
    }

    private static final class EqualsDecisionMaker<V> extends DecisionMaker<V> {
        private final DataType dataType;
        private final V expectedValue;
        private Decision decision;

        EqualsDecisionMaker(DataType dataType, V expectedValue) {
            this.dataType = dataType;
            this.expectedValue = expectedValue;
        }

        @Override
        public Decision decide(V existingValue, V providedValue) {
            assert decision == null;
            decision = !areValuesEqual(dataType, expectedValue, existingValue) ? Decision.ABORT :
                       providedValue == null ? Decision.REMOVE : Decision.PUT;
            return decision;
        }

        @Override
        public void reset() {
            decision = null;
        }

        Decision getDecision() {
            return decision;
        }

        @Override
        public String toString() {
            return "equals_to " + expectedValue;
        }
    }

    private static final class ContainsDecisionMaker<V> extends DecisionMaker<V> {
        private Decision decision;

        ContainsDecisionMaker() {
        }

        @Override
        public Decision decide(V existingValue, V providedValue) {
            assert decision == null;
            decision = existingValue == null ? Decision.ABORT : Decision.PUT;
            return decision;
        }

        @Override
        public <T extends V> T selectValue(T existingValue, T providedValue) {
            return existingValue;
        }

        @Override
        public void reset() {
            decision = null;
        }

        Decision getDecision() {
            return decision;
        }

        @Override
        public String toString() {
            return "contains";
        }
    }

    private static final class IntValueHolder {
        int value;

        IntValueHolder() {
        }
    }
}
