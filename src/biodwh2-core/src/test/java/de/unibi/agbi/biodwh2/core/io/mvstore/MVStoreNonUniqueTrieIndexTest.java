package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MVStoreNonUniqueTrieIndexTest {
    @Test
    void putTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreNonUniqueTrieIndexTest.putTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreNonUniqueTrieIndex(db, "index", "test", false);
            final MVStoreId id = new MVStoreId();
            index.put("value", id.getIdValue());
            final Set<Long> foundIds = index.find("value");
            assertEquals(1, foundIds.size());
            assertEquals(id.getIdValue(), foundIds.stream().findFirst().get());
        }
    }

    @Test
    void putArrayTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreNonUniqueTrieIndexTest.putArrayTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreNonUniqueTrieIndex(db, "index", "test", true);
            final MVStoreId id = new MVStoreId();
            final String[] array = new String[]{"value1", "value2", "value3"};
            index.put(array, id.getIdValue());
            for (final String value : array) {
                final Set<Long> foundIds = index.find(value);
                assertEquals(1, foundIds.size());
                assertEquals(id.getIdValue(), foundIds.stream().findFirst().get());
            }
        }
    }

    @Test
    void removeTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreNonUniqueTrieIndexTest.removeTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreNonUniqueTrieIndex(db, "index", "test", false);
            final String indexKey = "value";
            final MVStoreId[] ids = new MVStoreId[25];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = new MVStoreId();
                index.put(indexKey, ids[i].getIdValue());
            }
            // Validate that the index holds all ids
            Set<Long> foundIds = index.find(indexKey);
            assertEquals(ids.length, foundIds.size());
            for (final MVStoreId id : ids)
                assertTrue(foundIds.contains(id.getIdValue()));
            // Remove the first id
            index.remove(indexKey, ids[0].getIdValue());
            foundIds = index.find(indexKey);
            assertEquals(ids.length - 1, foundIds.size());
            for (int i = 1; i < ids.length; i++)
                assertTrue(foundIds.contains(ids[i].getIdValue()));
            // Remove the last id
            index.remove(indexKey, ids[ids.length - 1].getIdValue());
            foundIds = index.find(indexKey);
            assertEquals(ids.length - 2, foundIds.size());
            for (int i = 1; i < ids.length - 1; i++)
                assertTrue(foundIds.contains(ids[i].getIdValue()));
            // Remove an id in between
            index.remove(indexKey, ids[ids.length / 2].getIdValue());
            foundIds = index.find(indexKey);
            assertEquals(ids.length - 3, foundIds.size());
            for (int i = 1; i < ids.length - 1; i++)
                if (i == ids.length / 2)
                    assertFalse(foundIds.contains(ids[i].getIdValue()));
                else
                    assertTrue(foundIds.contains(ids[i].getIdValue()));
        }
    }
}