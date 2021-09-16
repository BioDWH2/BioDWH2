package de.unibi.agbi.biodwh2.core.collections;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ReverseLongTrieTest {
    @Test
    void addTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        assertFalse(trie.add(null));
        assertTrue(trie.add(4L));
        assertTrue(trie.add(10L));
        assertTrue(trie.add(101L));
        assertEquals(3, trie.size());
    }

    @Test
    void handleZeroTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        assertTrue(trie.add(0L));
        assertEquals(1, trie.size());
        assertTrue(trie.contains(0L));
        assertTrue(trie.remove(0L));
        assertEquals(0, trie.size());
        assertFalse(trie.contains(0L));
    }

    @Test
    void addIsDistinctTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(5L);
        assertEquals(1, trie.size());
        trie.add(5L);
        assertEquals(1, trie.size());
    }

    @Test
    void sizeTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(5L);
        assertEquals(1, trie.size());
        trie.add(55L);
        assertEquals(2, trie.size());
        trie.add(333L);
        assertEquals(3, trie.size());
    }

    @Test
    void containsTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(389593L);
        assertTrue(trie.contains(389593L));
    }

    @Test
    void removeTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(389593L);
        assertTrue(trie.contains(389593L));
        assertTrue(trie.remove(389593L));
        assertFalse(trie.contains(389593L));
    }

    @Test
    void removeIntermediateLeafTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(321L);
        trie.add(654321L);
        assertFalse(trie.remove(321L));
        assertFalse(trie.contains(321L));
        assertTrue(trie.contains(654321L));
    }

    @Test
    void valuesTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        assertEquals(0, trie.values().size());
        trie.add(5L);
        assertArrayEquals(new Long[]{5L}, trie.values().stream().sorted().toArray());
        trie.add(55L);
        assertArrayEquals(new Long[]{5L, 55L}, trie.values().stream().sorted().toArray());
        trie.add(56L);
        assertArrayEquals(new Long[]{5L, 55L, 56L}, trie.values().stream().sorted().toArray());
        trie.add(555L);
        assertArrayEquals(new Long[]{5L, 55L, 56L, 555L}, trie.values().stream().sorted().toArray());
    }

    @Test
    void isEmptyTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(555L);
        assertFalse(trie.isEmpty());
        trie.remove(555L);
        assertTrue(trie.isEmpty());
    }

    @Test
    void clearTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(5L);
        trie.add(10L);
        trie.add(3490L);
        assertFalse(trie.isEmpty());
        trie.clear();
        //noinspection ConstantConditions
        assertTrue(trie.isEmpty());
    }

    @Test
    void concurrentModificationTest() {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(5L);
        trie.add(55L);
        trie.add(555L);
        trie.add(554L);
        trie.add(10L);
        trie.add(3490L);
        assertDoesNotThrow(() -> {
            for (final Long value : trie)
                trie.remove(value);
        });
    }

    @Test
    void serializableTest() throws IOException, ClassNotFoundException {
        final ReverseLongTrie trie = new ReverseLongTrie();
        trie.add(321L);
        trie.add(654321L);
        trie.add(0L);
        trie.add(2390548L);
        trie.add(2304903533523L);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        new ObjectOutputStream(output).writeObject(trie);
        final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(output.toByteArray()));
        final ReverseLongTrie loadedTrie = (ReverseLongTrie) ois.readObject();
        assertArrayEquals(trie.values().stream().sorted().toArray(), loadedTrie.values().stream().sorted().toArray());
    }
}