package de.unibi.agbi.biodwh2.core.collections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {
    @Test
    void addTest() {
        final Trie trie = new Trie();
        trie.add("a");
        trie.add("ab");
        trie.add("ababa");
        assertEquals(3, trie.size());
    }

    @Test
    void addIsDistinctTest() {
        final Trie trie = new Trie();
        trie.add("a");
        assertEquals(1, trie.size());
        trie.add("a");
        assertEquals(1, trie.size());
    }

    @Test
    void sizeTest() {
        final Trie trie = new Trie();
        trie.add("a");
        assertEquals(1, trie.size());
        trie.add("ab");
        assertEquals(2, trie.size());
        trie.add("ababa");
        assertEquals(3, trie.size());
    }

    @Test
    void containsTest() {
        final Trie trie = new Trie();
        trie.add("test");
        assertTrue(trie.contains("test"));
    }

    @Test
    void removeTest() {
        final Trie trie = new Trie();
        trie.add("a");
        assertTrue(trie.contains("a"));
        assertTrue(trie.remove("a"));
        assertFalse(trie.contains("test"));
    }

    @Test
    void removeIntermediateLeafTest() {
        final Trie trie = new Trie();
        trie.add("paint");
        trie.add("painter");
        assertFalse(trie.remove("paint"));
        assertFalse(trie.contains("paint"));
        assertTrue(trie.contains("painter"));
    }

    @Test
    void valuesTest() {
        final Trie trie = new Trie();
        assertEquals(0, trie.values().size());
        trie.add("a");
        assertArrayEquals(new String[]{"a"}, trie.values().stream().sorted().toArray());
        trie.add("aa");
        assertArrayEquals(new String[]{"a", "aa"}, trie.values().stream().sorted().toArray());
        trie.add("ab");
        assertArrayEquals(new String[]{"a", "aa", "ab"}, trie.values().stream().sorted().toArray());
        trie.add("aba");
        assertArrayEquals(new String[]{"a", "aa", "ab", "aba"}, trie.values().stream().sorted().toArray());
    }

    @Test
    void isEmptyTest() {
        final Trie trie = new Trie();
        trie.add("a");
        assertFalse(trie.isEmpty());
        trie.remove("a");
        assertTrue(trie.isEmpty());
    }

    @Test
    void clearTest() {
        final Trie trie = new Trie();
        trie.add("a");
        trie.add("ab");
        trie.add("aa");
        assertFalse(trie.isEmpty());
        trie.clear();
        //noinspection ConstantConditions
        assertTrue(trie.isEmpty());
    }
}