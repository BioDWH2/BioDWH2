package de.unibi.agbi.biodwh2.core.io.flatfile;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class FlatFileReaderTest {
    @Test
    void testReadingFromFileStream() throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("test_flatfile.dat")) {
            final FlatFileReader reader = new FlatFileReader(stream, StandardCharsets.UTF_8);
            final Iterator<FlatFileEntry> entryIterator = reader.iterator();
            // Entry 1
            assertTrue(entryIterator.hasNext());
            FlatFileEntry entry = entryIterator.next();
            assertEquals("ID", entry.properties.get(0).get(0).key);
            assertEquals("cel-let-7         standard; RNA; CEL; 99 BP.", entry.properties.get(0).get(0).value);
            assertEquals("AC", entry.properties.get(1).get(0).key);
            assertEquals("MI0000001;", entry.properties.get(1).get(0).value);
            assertEquals("RN", entry.properties.get(3).get(0).key);
            assertEquals("[1]", entry.properties.get(3).get(0).value);
            assertEquals("RX", entry.properties.get(3).get(1).key);
            assertEquals("PUBMED; 11679671.", entry.properties.get(3).get(1).value);
            assertEquals("RA", entry.properties.get(3).get(2).key);
            assertEquals("Lau NC, Lim LP, Weinstein EG, Bartel DP;", entry.properties.get(3).get(2).value);
            assertEquals("RT", entry.properties.get(3).get(3).key);
            assertEquals(
                    "\"An abundant class of tiny RNAs with probable regulatory roles in\nCaenorhabditis elegans\";",
                    entry.properties.get(3).get(3).value);
            assertEquals("SQ", entry.properties.get(13).get(0).key);
            assertEquals("Sequence 99 BP; 26 A; 19 C; 24 G; 0 T; 30 other;\n" +
                         "uacacugugg auccggugag guaguagguu guauaguuug gaauauuacc accggugaac        60\n" +
                         "uaugcaauuu ucuaccuuac cggagacaga acucuucga                               99",
                         entry.properties.get(13).get(0).value);
            // Entry 2
            assertTrue(entryIterator.hasNext());
            entry = entryIterator.next();
            assertEquals("ID", entry.properties.get(0).get(0).key);
            assertEquals("cel-lin-4         standard; RNA; CEL; 94 BP.", entry.properties.get(0).get(0).value);
            assertEquals("AC", entry.properties.get(1).get(0).key);
            assertEquals("MI0000002;", entry.properties.get(1).get(0).value);
            // Entry 3
            assertTrue(entryIterator.hasNext());
            entry = entryIterator.next();
            assertEquals("ID", entry.properties.get(0).get(0).key);
            assertEquals("cel-mir-1         standard; RNA; CEL; 96 BP.", entry.properties.get(0).get(0).value);
            assertEquals("AC", entry.properties.get(1).get(0).key);
            assertEquals("MI0000003;", entry.properties.get(1).get(0).value);
            assertFalse(entryIterator.hasNext());
        }
    }
}