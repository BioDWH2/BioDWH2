package de.unibi.agbi.biodwh2.ttd.etl;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class FlatFileTTDReaderTest {
    private static final String ENTRIES_TEXT =
            "T1\tID\tT1\t\t\n" + "T1\tNAME\tTest 1\t\t\n" + "T1\tARRAY\tABC\t\t\n" + "T1\tARRAY\tXYZ\t\t\n" +
            "T2\tTARGETID\tT2\t\t\n" + "T2\tNAME\tTest 2\t\t\n" + "\n" + "T3\tTARGETID\tT3\t\t\n" +
            "T3\tNAME\tTest 3\t\t\n";

    @Test
    void readEntries() {
        final InputStream input = new ByteArrayInputStream(ENTRIES_TEXT.getBytes(StandardCharsets.UTF_8));
        try (FlatFileTTDReader reader = new FlatFileTTDReader(input, StandardCharsets.UTF_8)) {
            final Iterator<FlatFileTTDEntry> entries = reader.iterator();
            assertTrue(entries.hasNext());
            FlatFileTTDEntry entry = entries.next();
            assertEquals("T1", entry.getID());
            assertTrue(entries.hasNext());
            entry = entries.next();
            assertEquals("T2", entry.getID());
            assertTrue(entries.hasNext());
            entry = entries.next();
            assertEquals("T3", entry.getID());
            assertFalse(entries.hasNext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}