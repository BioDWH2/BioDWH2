package de.unibi.agbi.biodwh2.core.io.sdf;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class SdfReaderTest {
    @Test
    void testReadingFromFileStream() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File(classLoader.getResource("test_pubchem.sdf").getFile());
        assertTrue(file.exists());
        InputStream stream = new FileInputStream(file);
        SdfReader reader = new SdfReader(stream, "UTF-8");
        Iterator<SdfEntry> entryIterator = reader.iterator();
        assertTrue(entryIterator.hasNext());
        SdfEntry entry = entryIterator.next();
        assertEquals("1", entry.title);
        assertTrue(entry.properties.containsKey("PUBCHEM_IUPAC_CAS_NAME"));
        assertEquals("3-acetyloxy-4-(trimethylammonio)butanoate", entry.properties.get("PUBCHEM_IUPAC_CAS_NAME"));
        assertTrue(entryIterator.hasNext());
        entry = entryIterator.next();
        assertEquals("2", entry.title);
        assertTrue(entry.properties.containsKey("PUBCHEM_IUPAC_CAS_NAME"));
        assertEquals("(2-acetyloxy-3-carboxypropyl)-trimethylammonium", entry.properties.get("PUBCHEM_IUPAC_CAS_NAME"));
        assertFalse(entryIterator.hasNext());
    }
}