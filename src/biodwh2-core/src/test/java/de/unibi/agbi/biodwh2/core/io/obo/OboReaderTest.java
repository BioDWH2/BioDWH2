package de.unibi.agbi.biodwh2.core.io.obo;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class OboReaderTest {
    @Test
    void testReadingFromFileStream() throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("test_go.obo")) {
            final OboReader reader = new OboReader(stream, "UTF-8");
            assertNotNull(reader.getHeader());
            assertEquals("1.2", reader.getHeader().getFormatVersion());
            assertEquals(16, reader.getHeader().get("subsetdef").length);
            final Iterator<OboEntry> entryIterator = reader.iterator();
            assertTrue(entryIterator.hasNext());
            final OboEntry entry = entryIterator.next();
            assertEquals("Term", entry.getType());
            assertEquals("GO:0000001", entry.getFirst("id"));
            assertEquals("GO:0048308", entry.getFirst("is_a"));
        }
    }
}
