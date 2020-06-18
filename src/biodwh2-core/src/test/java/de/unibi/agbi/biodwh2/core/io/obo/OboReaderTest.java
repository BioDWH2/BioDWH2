package de.unibi.agbi.biodwh2.core.io.obo;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class OboReaderTest {
    @Test
    void testReadingFromFileStream() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource("test_go.obo").getFile());
        assertTrue(file.exists());
        InputStream stream = new FileInputStream(file);
        OboReader reader = new OboReader(stream, "UTF-8");
        OboEntry header = reader.getHeader();
        assertNotNull(header);
        assertEquals("1.2", header.getFirst("format-version"));
        assertEquals(16, header.get("subsetdef").length);
        Iterator<OboEntry> entryIterator = reader.iterator();
        assertTrue(entryIterator.hasNext());
        OboEntry entry = entryIterator.next();
        assertEquals("Term", entry.getName());
        assertEquals("GO:0000001", entry.getFirst("id"));
        assertEquals("GO:0048308", entry.getFirst("is_a"));
    }
}
