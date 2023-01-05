/*
 * Copyright 2004-2019 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 *
 * Modified to a minimal version for BioDWH2 graph databases
 */
package org.h2.mvstore;

import java.lang.ref.WeakReference;

/**
 * A scan resistant cache that uses keys of type long. It is meant to cache objects that are relatively costly to
 * acquire, for example file content.
 * <p>
 * This implementation is multi-threading safe and supports concurrent access. Null keys or null values are not allowed.
 * The map fill factor is at most 75%.
 * <p>
 * Each entry is assigned a distinct memory size, and the cache will try to use at most the specified amount of memory.
 * The memory unit is not relevant, however it is suggested to use bytes as the unit.
 * <p>
 * This class implements an approximation of the LIRS replacement algorithm invented by Xiaodong Zhang and Song Jiang as
 * described in http://www.cse.ohio-state.edu/~zhang/lirs-sigmetrics-02.html with a few smaller changes: An additional
 * queue for non-resident entries is used, to prevent unbound memory usage. The maximum size of this queue is at most
 * the size of the rest of the stack. About 6.25% of the mapped entries are cold.
 * <p>
 * Internally, the cache is split into a number of segments, and each segment is an individual LIRS cache.
 * <p>
 * Accessed entries are only moved to the top of the stack if at least a number of other entries have been moved to the
 * front (8 per segment by default). Write access and moving entries to the top of the stack is synchronized per
 * segment.
 *
 * @param <V> the value type
 * @author Thomas Mueller
 */
public class CacheLongKeyLIRS<V> {
    /**
     * The maximum memory this cache should use.
     */
    private static final long MAX_MEMORY = 16 * 1024L * 1024L;

    /**
     * The number of cache segments (must be a power of 2).
     */
    private static final int SEGMENT_COUNT = 16;

    /**
     * How many other item are to be moved to the top of the stack before the current item is moved.
     */
    private static final int STACK_MOVE_DISTANCE = 32;

    /**
     * Low water mark for the number of entries in the non-resident queue, as a factor of the number of all other
     * entries in the map.
     */
    private static final int NON_RESIDENT_QUEUE_SIZE = 3;

    /**
     * High watermark for the number of entries in the non-resident queue, as a factor of the number of all other
     * entries in the map
     */
    private static final int NON_RESIDENT_QUEUE_SIZE_HIGH = 12;

    private final Segment<V>[] segments;
    private final int segmentShift;
    private final int segmentMask;

    /**
     * Create a new cache with the given memory size.
     */
    @SuppressWarnings("unchecked")
    public CacheLongKeyLIRS() {
        this.segmentMask = SEGMENT_COUNT - 1;
        segments = new Segment[SEGMENT_COUNT];
        clear();
        // use the high bits for the segment
        this.segmentShift = 32 - Integer.bitCount(segmentMask);
    }

    /**
     * Remove all entries.
     */
    public void clear() {
        long max = getMaxItemSize();
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            segments[i] = new Segment<>(max, STACK_MOVE_DISTANCE, 8, NON_RESIDENT_QUEUE_SIZE,
                                        NON_RESIDENT_QUEUE_SIZE_HIGH);
        }
    }

    /**
     * Determines max size of the data item size to fit into cache
     *
     * @return data items size limit
     */
    public long getMaxItemSize() {
        return Math.max(1, MAX_MEMORY / SEGMENT_COUNT);
    }

    private Entry<V> find(long key) {
        int hash = getHash(key);
        return getSegment(hash).find(key, hash);
    }

    /**
     * Check whether there is a resident entry for the given key. This method does not adjust the internal state of the
     * cache.
     *
     * @param key the key (may not be null)
     * @return true if there is a resident entry
     */
    public boolean containsKey(long key) {
        Entry<V> e = find(key);
        return e != null && e.value != null;
    }

    /**
     * Add an entry to the cache using the average memory size.
     *
     * @param key   the key (may not be null)
     * @param value the value (may not be null)
     * @return the old value, or null if there was no resident entry
     */
    public V put(long key, V value) {
        return put(key, value, sizeOf(value));
    }

    /**
     * Add an entry to the cache. The entry may or may not exist in the cache yet. This method will usually mark unknown
     * entries as cold and known entries as hot.
     *
     * @param key    the key (may not be null)
     * @param value  the value (may not be null)
     * @param memory the memory used for the given entry
     * @return the old value, or null if there was no resident entry
     */
    public V put(long key, V value, int memory) {
        if (value == null) {
            throw DataUtils.newIllegalArgumentException("The value may not be null");
        }
        int hash = getHash(key);
        int segmentIndex = getSegmentIndex(hash);
        Segment<V> s = segments[segmentIndex];
        // check whether resize is required: synchronize on s, to avoid
        // concurrent resizes (concurrent reads read
        // from the old segment)
        synchronized (s) {
            s = resizeIfNeeded(s, segmentIndex);
            return s.put(key, hash, value, memory);
        }
    }

    private Segment<V> resizeIfNeeded(Segment<V> s, int segmentIndex) {
        int newLen = s.getNewMapLen();
        if (newLen == 0) {
            return s;
        }
        // another thread might have resized
        // (as we retrieved the segment before synchronizing on it)
        Segment<V> s2 = segments[segmentIndex];
        if (s == s2) {
            // no other thread resized, so we do
            s = new Segment<>(s, newLen);
            segments[segmentIndex] = s;
        }
        return s;
    }

    /**
     * Get the size of the given value. The default implementation returns 1.
     *
     * @param value the value
     * @return the size
     */
    @SuppressWarnings("unused")
    protected int sizeOf(V value) {
        return 1;
    }

    /**
     * Get the value for the given key if the entry is cached. This method adjusts the internal state of the cache
     * sometimes, to ensure commonly used entries stay in the cache.
     *
     * @param key the key (may not be null)
     * @return the value, or null if there is no resident entry
     */
    public V get(long key) {
        int hash = getHash(key);
        Segment<V> s = getSegment(hash);
        Entry<V> e = s.find(key, hash);
        return s.get(e);
    }

    private Segment<V> getSegment(int hash) {
        return segments[getSegmentIndex(hash)];
    }

    private int getSegmentIndex(int hash) {
        return (hash >>> segmentShift) & segmentMask;
    }

    /**
     * Get the hash code for the given key. The hash code is further enhanced to spread the values more evenly.
     *
     * @param key the key
     * @return the hash code
     */
    static int getHash(long key) {
        int hash = (int) ((key >>> 32) ^ key);
        // a supplemental secondary hash function
        // to protect against hash codes that don't differ much
        hash = ((hash >>> 16) ^ hash) * 0x45d9f3b;
        hash = ((hash >>> 16) ^ hash) * 0x45d9f3b;
        hash = (hash >>> 16) ^ hash;
        return hash;
    }

    /**
     * A cache segment
     *
     * @param <V> the value type
     */
    private static class Segment<V> {

        /**
         * The number of (hot, cold, and non-resident) entries in the map.
         */
        int mapSize;

        /**
         * The size of the LIRS queue for resident cold entries.
         */
        int queueSize;

        /**
         * The size of the LIRS queue for non-resident cold entries.
         */
        int queue2Size;

        /**
         * The number of cache hits.
         */
        long hits;

        /**
         * The number of cache misses.
         */
        long misses;

        /**
         * The map array. The size is always a power of 2.
         */
        final Entry<V>[] entries;

        /**
         * The currently used memory.
         */
        long usedMemory;

        /**
         * How many other item are to be moved to the top of the stack before the current item is moved.
         */
        private final int stackMoveDistance;

        /**
         * The maximum memory this cache should use in bytes.
         */
        private long maxMemory;

        /**
         * The bit mask that is applied to the key hash code to get the index in the map array. The mask is the length
         * of the array minus one.
         */
        private final int mask;

        /**
         * Low watermark for the number of entries in the non-resident queue, as a factor of the number of entries in
         * the map.
         */
        private final int nonResidentQueueSize;

        /**
         * High watermark for the number of entries in the non-resident queue, as a factor of the number of entries in
         * the map.
         */
        private final int nonResidentQueueSizeHigh;

        /**
         * The stack of recently referenced elements. This includes all hot entries, and the recently referenced cold
         * entries. Resident cold entries that were not recently referenced, as well as non-resident cold entries, are
         * not in the stack.
         * <p>
         * There is always at least one entry: the head entry.
         */
        private final Entry<V> stack;

        /**
         * The number of entries in the stack.
         */
        private int stackSize;

        /**
         * The queue of resident cold entries.
         * <p>
         * There is always at least one entry: the head entry.
         */
        private final Entry<V> queue;

        /**
         * The queue of non-resident cold entries.
         * <p>
         * There is always at least one entry: the head entry.
         */
        private final Entry<V> queue2;

        /**
         * The number of times any item was moved to the top of the stack.
         */
        private int stackMoveCounter;

        /**
         * Create a new cache segment.
         *
         * @param maxMemory                the maximum memory to use
         * @param stackMoveDistance        the number of other entries to be moved to the top of the stack before moving
         *                                 an entry to the top
         * @param len                      the number of hash table buckets (must be a power of 2)
         * @param nonResidentQueueSize     the non-resident queue size low watermark factor
         * @param nonResidentQueueSizeHigh the non-resident queue size high watermark factor
         */
        Segment(long maxMemory, int stackMoveDistance, int len, int nonResidentQueueSize,
                int nonResidentQueueSizeHigh) {
            setMaxMemory(maxMemory);
            this.stackMoveDistance = stackMoveDistance;
            this.nonResidentQueueSize = nonResidentQueueSize;
            this.nonResidentQueueSizeHigh = nonResidentQueueSizeHigh;

            // the bit mask has all bits set
            mask = len - 1;

            // initialize the stack and queue heads
            stack = new Entry<>();
            stack.stackPrev = stack.stackNext = stack;
            queue = new Entry<>();
            queue.queuePrev = queue.queueNext = queue;
            queue2 = new Entry<>();
            queue2.queuePrev = queue2.queueNext = queue2;

            @SuppressWarnings("unchecked") Entry<V>[] e = new Entry[len];
            entries = e;
        }

        /**
         * Create a new cache segment from an existing one. The caller must synchronize on the old segment, to avoid
         * concurrent modifications.
         *
         * @param old the old segment
         * @param len the number of hash table buckets (must be a power of 2)
         */
        Segment(Segment<V> old, int len) {
            this(old.maxMemory, old.stackMoveDistance, len, old.nonResidentQueueSize, old.nonResidentQueueSizeHigh);
            hits = old.hits;
            misses = old.misses;
            Entry<V> s = old.stack.stackPrev;
            while (s != old.stack) {
                Entry<V> e = new Entry<>(s);
                addToMap(e);
                addToStack(e);
                s = s.stackPrev;
            }
            s = old.queue.queuePrev;
            while (s != old.queue) {
                Entry<V> e = find(s.key, getHash(s.key));
                if (e == null) {
                    e = new Entry<>(s);
                    addToMap(e);
                }
                addToQueue(queue, e);
                s = s.queuePrev;
            }
            s = old.queue2.queuePrev;
            while (s != old.queue2) {
                Entry<V> e = find(s.key, getHash(s.key));
                if (e == null) {
                    e = new Entry<>(s);
                    addToMap(e);
                }
                addToQueue(queue2, e);
                s = s.queuePrev;
            }
        }

        /**
         * Calculate the new number of hash table buckets if the internal map should be re-sized.
         *
         * @return 0 if no resizing is needed, or the new length
         */
        int getNewMapLen() {
            int len = mask + 1;
            if (len * 3 < mapSize * 4 && len < (1 << 28)) {
                // more than 75% usage
                return len * 2;
            } else if (len > 32 && len / 8 > mapSize) {
                // less than 12% usage
                return len / 2;
            }
            return 0;
        }

        private void addToMap(Entry<V> e) {
            int index = getHash(e.key) & mask;
            e.mapNext = entries[index];
            entries[index] = e;
            usedMemory += e.getMemory();
            mapSize++;
        }

        /**
         * Get the value from the given entry. This method adjusts the internal state of the cache sometimes, to ensure
         * commonly used entries stay in the cache.
         *
         * @param e the entry
         * @return the value, or null if there is no resident entry
         */
        synchronized V get(Entry<V> e) {
            V value = e == null ? null : e.getValue();
            if (value == null) {
                // the entry was not found
                // or it was a non-resident entry
                misses++;
            } else {
                access(e);
                hits++;
            }
            return value;
        }

        /**
         * Access an item, moving the entry to the top of the stack or front of the queue if found.
         *
         * @param e entry to record access for
         */
        private void access(Entry<V> e) {
            if (e.isHot()) {
                if (e != stack.stackNext && e.stackNext != null) {
                    if (stackMoveCounter - e.topMove > stackMoveDistance) {
                        // move a hot entry to the top of the stack
                        // unless it is already there
                        boolean wasEnd = e == stack.stackPrev;
                        removeFromStack(e);
                        if (wasEnd) {
                            // if moving the last entry, the last entry
                            // could now be cold, which is not allowed
                            pruneStack();
                        }
                        addToStack(e);
                    }
                }
            } else {
                V v = e.getValue();
                if (v != null) {
                    removeFromQueue(e);
                    if (e.reference != null) {
                        e.value = v;
                        e.reference = null;
                        usedMemory += e.memory;
                    }
                    if (e.stackNext != null) {
                        // resident, or even non-resident (weak value reference),
                        // cold entries become hot if they are on the stack
                        removeFromStack(e);
                        // which means a hot entry needs to become cold
                        // (this entry is cold, that means there is at least one
                        // more entry in the stack, which must be hot)
                        convertOldestHotToCold();
                    } else {
                        // cold entries that are not on the stack
                        // move to the front of the queue
                        addToQueue(queue, e);
                    }
                    // in any case, the cold entry is moved to the top of the stack
                    addToStack(e);
                    // but if newly promoted cold/non-resident is the only entry on a stack now
                    // that means last one is cold, need to prune
                    pruneStack();
                }
            }
        }

        /**
         * Add an entry to the cache. The entry may or may not exist in the cache yet. This method will usually mark
         * unknown entries as cold and known entries as hot.
         *
         * @param key    the key (may not be null)
         * @param hash   the hash
         * @param value  the value (may not be null)
         * @param memory the memory used for the given entry
         * @return the old value, or null if there was no resident entry
         */
        synchronized V put(long key, int hash, V value, int memory) {
            Entry<V> e = find(key, hash);
            boolean existed = e != null;
            V old = null;
            if (existed) {
                old = e.getValue();
                remove(key, hash);
            }
            if (memory > maxMemory) {
                // the new entry is too big to fit
                return old;
            }
            e = new Entry<>(key, value, memory);
            int index = hash & mask;
            e.mapNext = entries[index];
            entries[index] = e;
            usedMemory += memory;
            if (usedMemory > maxMemory) {
                // old entries needs to be removed
                evict();
                // if the cache is full, the new entry is
                // cold if possible
                if (stackSize > 0) {
                    // the new cold entry is at the top of the queue
                    addToQueue(queue, e);
                }
            }
            mapSize++;
            // added entries are always added to the stack
            addToStack(e);
            if (existed) {
                // if it was there before (even non-resident), it becomes hot
                access(e);
            }
            return old;
        }

        /**
         * Remove an entry. Both resident and non-resident entries can be removed.
         *
         * @param key  the key (may not be null)
         * @param hash the hash
         * @return the old value, or null if there was no resident entry
         */
        synchronized V remove(long key, int hash) {
            int index = hash & mask;
            Entry<V> e = entries[index];
            if (e == null) {
                return null;
            }
            if (e.key == key) {
                entries[index] = e.mapNext;
            } else {
                Entry<V> last;
                do {
                    last = e;
                    e = e.mapNext;
                    if (e == null) {
                        return null;
                    }
                } while (e.key != key);
                last.mapNext = e.mapNext;
            }
            V old = e.getValue();
            mapSize--;
            usedMemory -= e.getMemory();
            if (e.stackNext != null) {
                removeFromStack(e);
            }
            if (e.isHot()) {
                // when removing a hot entry, the newest cold entry gets hot,
                // so the number of hot entries does not change
                e = queue.queueNext;
                if (e != queue) {
                    removeFromQueue(e);
                    if (e.stackNext == null) {
                        addToStackBottom(e);
                    }
                }
                pruneStack();
            } else {
                removeFromQueue(e);
            }
            return old;
        }

        /**
         * Evict cold entries (resident and non-resident) until the memory limit is reached. The new entry is added as a
         * cold entry, except if it is the only entry.
         */
        private void evict() {
            do {
                evictBlock();
            } while (usedMemory > maxMemory);
        }

        private void evictBlock() {
            // ensure there are not too many hot entries: right shift of 5 is
            // division by 32, that means if there are only 1/32 (3.125%) or
            // less cold entries, a hot entry needs to become cold
            while (queueSize <= ((mapSize - queue2Size) >>> 5) && stackSize > 0) {
                convertOldestHotToCold();
            }
            // the oldest resident cold entries become non-resident
            while (usedMemory > maxMemory && queueSize > 0) {
                Entry<V> e = queue.queuePrev;
                usedMemory -= e.memory;
                removeFromQueue(e);
                e.reference = new WeakReference<>(e.value);
                e.value = null;
                addToQueue(queue2, e);
                // the size of the non-resident-cold entries needs to be limited
                trimNonResidentQueue();
            }
        }

        void trimNonResidentQueue() {
            int residentCount = mapSize - queue2Size;
            int maxQueue2SizeHigh = nonResidentQueueSizeHigh * residentCount;
            int maxQueue2Size = nonResidentQueueSize * residentCount;
            while (queue2Size > maxQueue2Size) {
                Entry<V> e = queue2.queuePrev;
                if (queue2Size <= maxQueue2SizeHigh) {
                    WeakReference<V> reference = e.reference;
                    if (reference != null && reference.get() != null) {
                        break;  // stop trimming if entry holds a value
                    }
                }
                int hash = getHash(e.key);
                remove(e.key, hash);
            }
        }

        private void convertOldestHotToCold() {
            // the last entry of the stack is known to be hot
            Entry<V> last = stack.stackPrev;
            if (last == stack) {
                // never remove the stack head itself (this would mean the
                // internal structure of the cache is corrupt)
                throw new IllegalStateException();
            }
            // remove from stack - which is done anyway in the stack pruning,
            // but we can do it here as well
            removeFromStack(last);
            // adding an entry to the queue will make it cold
            addToQueue(queue, last);
            pruneStack();
        }

        /**
         * Ensure the last entry of the stack is cold.
         */
        private void pruneStack() {
            while (true) {
                Entry<V> last = stack.stackPrev;
                // must stop at a hot entry or the stack head,
                // but the stack head itself is also hot, so we
                // don't have to test it
                if (last.isHot()) {
                    break;
                }
                // the cold entry is still in the queue
                removeFromStack(last);
            }
        }

        /**
         * Try to find an entry in the map.
         *
         * @param key  the key
         * @param hash the hash
         * @return the entry (might be a non-resident)
         */
        Entry<V> find(long key, int hash) {
            int index = hash & mask;
            Entry<V> e = entries[index];
            while (e != null && e.key != key) {
                e = e.mapNext;
            }
            return e;
        }

        private void addToStack(Entry<V> e) {
            e.stackPrev = stack;
            e.stackNext = stack.stackNext;
            e.stackNext.stackPrev = e;
            stack.stackNext = e;
            stackSize++;
            e.topMove = stackMoveCounter++;
        }

        private void addToStackBottom(Entry<V> e) {
            e.stackNext = stack;
            e.stackPrev = stack.stackPrev;
            e.stackPrev.stackNext = e;
            stack.stackPrev = e;
            stackSize++;
        }

        /**
         * Remove the entry from the stack. The head itself must not be removed.
         *
         * @param e the entry
         */
        private void removeFromStack(Entry<V> e) {
            e.stackPrev.stackNext = e.stackNext;
            e.stackNext.stackPrev = e.stackPrev;
            e.stackPrev = e.stackNext = null;
            stackSize--;
        }

        private void addToQueue(Entry<V> q, Entry<V> e) {
            e.queuePrev = q;
            e.queueNext = q.queueNext;
            e.queueNext.queuePrev = e;
            q.queueNext = e;
            if (e.value != null) {
                queueSize++;
            } else {
                queue2Size++;
            }
        }

        private void removeFromQueue(Entry<V> e) {
            e.queuePrev.queueNext = e.queueNext;
            e.queueNext.queuePrev = e.queuePrev;
            e.queuePrev = e.queueNext = null;
            if (e.value != null) {
                queueSize--;
            } else {
                queue2Size--;
            }
        }

        /**
         * Set the maximum memory this cache should use. This will not immediately cause entries to get removed however;
         * it will only change the limit. To resize the internal array, call the clear method.
         *
         * @param maxMemory the maximum size (1 or larger) in bytes
         */
        void setMaxMemory(long maxMemory) {
            this.maxMemory = maxMemory;
        }
    }

    /**
     * A cache entry. Each entry is either hot (low inter-reference recency; LIR), cold (high inter-reference recency;
     * HIR), or non-resident-cold. Hot entries are in the stack only. Cold entries are in the queue, and may be in the
     * stack. Non-resident-cold entries have their value set to null and are in the stack and in the non-resident
     * queue.
     *
     * @param <V> the value type
     */
    static class Entry<V> {
        /**
         * The key.
         */
        final long key;

        /**
         * The value. Set to null for non-resident-cold entries.
         */
        V value;

        /**
         * Weak reference to the value. Set to null for resident entries.
         */
        WeakReference<V> reference;

        /**
         * The estimated memory used.
         */
        final int memory;

        /**
         * When the item was last moved to the top of the stack.
         */
        int topMove;

        /**
         * The next entry in the stack.
         */
        Entry<V> stackNext;

        /**
         * The previous entry in the stack.
         */
        Entry<V> stackPrev;

        /**
         * The next entry in the queue (either the resident queue or the non-resident queue).
         */
        Entry<V> queueNext;

        /**
         * The previous entry in the queue.
         */
        Entry<V> queuePrev;

        /**
         * The next entry in the map (the chained entry).
         */
        Entry<V> mapNext;


        Entry() {
            this(0L, null, 0);
        }

        Entry(long key, V value, int memory) {
            this.key = key;
            this.memory = memory;
            this.value = value;
        }

        Entry(Entry<V> old) {
            this(old.key, old.value, old.memory);
            this.reference = old.reference;
            this.topMove = old.topMove;
        }

        /**
         * Whether this entry is hot. Cold entries are in one of the two queues.
         *
         * @return whether the entry is hot
         */
        boolean isHot() {
            return queueNext == null;
        }

        V getValue() {
            return value == null ? reference.get() : value;
        }

        int getMemory() {
            return value == null ? 0 : memory;
        }
    }
}
