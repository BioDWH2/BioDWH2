package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MVStoreNonUniqueIndexTest {
    @Test
    void putTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreNonUniqueIndexTest.putTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreNonUniqueIndex(db, "index", "test", false);
            final MVStoreId id = new MVStoreId();
            index.put("value", id.getIdValue());
            final Set<Long> foundIds = index.find("value");
            assertEquals(1, foundIds.size());
            assertEquals(id.getIdValue(), foundIds.stream().findFirst().get());
        }
    }

    @Test
    void putArrayTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreNonUniqueIndexTest.putArrayTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreNonUniqueIndex(db, "index", "test", true);
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
        final Path tempFilePath = Files.createTempFile("MVStoreNonUniqueIndexTest.removeTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreNonUniqueIndex(db, "index", "test", false, 50);
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
            // Remove the first id (min id in page metadata)
            index.remove(indexKey, ids[0].getIdValue());
            foundIds = index.find(indexKey);
            assertEquals(ids.length - 1, foundIds.size());
            for (int i = 1; i < ids.length; i++)
                assertTrue(foundIds.contains(ids[i].getIdValue()));
            // Remove the last id (max id in page metadata)
            index.remove(indexKey, ids[ids.length - 1].getIdValue());
            foundIds = index.find(indexKey);
            assertEquals(ids.length - 2, foundIds.size());
            for (int i = 1; i < ids.length - 1; i++)
                assertTrue(foundIds.contains(ids[i].getIdValue()));
            // Remove an id in between min/max
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

    @Test
    void pagesTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreNonUniqueIndexTest.pagesTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index1 = new MVStoreNonUniqueIndex(db, "index1", "i", false, 10);
            final MVStoreIndex index2 = new MVStoreNonUniqueIndex(db, "index2", "label", false, 10);
            for (int i = 0; i < 55; i++) {
                final MVStoreId id = new MVStoreId();
                index1.put(i, id.getIdValue());
                index2.put("TEST", id.getIdValue());
                assertEquals(i + 1, index2.find("TEST").size());
                final Set<Long> foundIds = index1.find(i);
                assertEquals(1, foundIds.size());
                assertEquals(id.getIdValue(), foundIds.stream().findFirst().get());
            }
        }
    }
}