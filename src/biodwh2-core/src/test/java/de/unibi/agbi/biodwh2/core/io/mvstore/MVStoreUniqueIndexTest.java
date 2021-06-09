package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MVStoreUniqueIndexTest {
    @Test
    void putTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreUniqueIndexTest.putTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreUniqueIndex(db, "index", "test", false);
            final MVStoreId id = new MVStoreId();
            index.put("value", id.getIdValue());
            final Set<Long> foundIds = index.find("value");
            assertEquals(1, foundIds.size());
            assertEquals(id.getIdValue(), foundIds.stream().findFirst().get());
        }
    }

    @Test
    void putUniqueKeyTwiceShouldThrowTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreUniqueIndexTest.putUniqueKeyTwiceShouldThrowTest",
                                                       ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreUniqueIndex(db, "index", "test", false);
            final MVStoreId id = new MVStoreId();
            index.put("value", id.getIdValue());
            assertThrows(MVStoreIndexException.class, () -> index.put("value", id.getIdValue()));
        }
    }

    @Test
    void removeTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreUniqueIndexTest.removeTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreIndex index = new MVStoreUniqueIndex(db, "index", "test", false);
            final MVStoreId id1 = new MVStoreId();
            final MVStoreId id2 = new MVStoreId();
            index.put("value1", id1.getIdValue());
            index.put("value2", id2.getIdValue());
            assertEquals(1, index.find("value1").size());
            assertEquals(1, index.find("value2").size());
            index.remove("value2", id1.getIdValue());
            assertEquals(1, index.find("value1").size());
            assertEquals(0, index.find("value2").size());
        }
    }
}
