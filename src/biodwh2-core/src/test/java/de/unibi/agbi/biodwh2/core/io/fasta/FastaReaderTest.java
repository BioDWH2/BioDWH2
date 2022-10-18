package de.unibi.agbi.biodwh2.core.io.fasta;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class FastaReaderTest {
    @Test
    void testReadingFromFileStream() throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("test_fasta.fa")) {
            final FastaReader reader = new FastaReader(stream, StandardCharsets.UTF_8);
            final Iterator<FastaEntry> entryIterator = reader.iterator();
            assertTrue(entryIterator.hasNext());
            FastaEntry entry = entryIterator.next();
            assertEquals(">cel-let-7 MI0000001 Caenorhabditis elegans let-7 stem-loop", entry.getHeader());
            assertEquals(
                    "UACACUGUGGAUCCGGUGAGGUAGUAGGUUGUAUAGUUUGGAAUAUUACCACCGGUGAACUAUGCAAUUUUCUACCUUACCGGAGACAGAACUCUUCGA",
                    entry.getSequence());
            assertTrue(entryIterator.hasNext());
            entry = entryIterator.next();
            assertEquals(">cel-lin-4 MI0000002 Caenorhabditis elegans lin-4 stem-loop", entry.getHeader());
            assertEquals(
                    "AUGCUUCCGGCCUGUUCCCUGAGACCUCAAGUGUGAGUGUACUAUUGAUGCUUCACACCUGGGCUCUCCGGGUACCAGGACGGUUUGAGCAGAU",
                    entry.getSequence());
            assertTrue(entryIterator.hasNext());
            entry = entryIterator.next();
            assertEquals(">cel-mir-1 MI0000003 Caenorhabditis elegans miR-1 stem-loop", entry.getHeader());
            assertEquals(
                    "AAAGUGACCGUACCGAGCUGCAUACUUCCUUACAUGCCCAUACUAUAUCAUAAAUGGAUAUGGAAUGUAAAGAAGUAUGUAGAACGGGGUGGUAGU",
                    entry.getSequence());
            assertTrue(entryIterator.hasNext());
            entry = entryIterator.next();
            assertEquals(">cel-mir-2 MI0000004 Caenorhabditis elegans miR-2 stem-loop", entry.getHeader());
            assertEquals(
                    "UAAACAGUAUACAGAAAGCCAUCAAAGCGGUGGUUGAUGUGUUGCAAAUUAUGACUUUCAUAUCACAGCCAGCUUUGAUGUGCUGCCUGUUGCACUGU",
                    entry.getSequence());
            assertFalse(entryIterator.hasNext());
        }
    }
}