package de.unibi.agbi.biodwh2.core.io.gff;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class GFF3ReaderTest {
    @Test
    void testReadingFromFileStream() throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("test_gff3.gff")) {
            final GFF3Reader reader = new GFF3Reader(stream, "UTF-8");
            GFF3Entry entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3PragmaEntry);
            assertEquals("gff-version 3.1.26", ((GFF3PragmaEntry) entry).getValue());
            entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3PragmaEntry);
            assertEquals("sequence-region ctg123 1 1497228", ((GFF3PragmaEntry) entry).getValue());
            entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3DataEntry);
            assertEquals("ctg123", ((GFF3DataEntry) entry).getSeqId());
            assertNull(((GFF3DataEntry) entry).getSource());
            assertEquals("SO:0000704", ((GFF3DataEntry) entry).getTypeSOId());
            assertEquals("gene", ((GFF3DataEntry) entry).getTypeSOName());
            assertEquals(1000L, ((GFF3DataEntry) entry).getStart());
            assertEquals(9000L, ((GFF3DataEntry) entry).getEnd());
            assertNull(((GFF3DataEntry) entry).getScore());
            assertEquals(GFF3DataEntry.Strand.POSITIVE, ((GFF3DataEntry) entry).getStrand());
            assertNull(((GFF3DataEntry) entry).getPhase());
            assertEquals("gene00001", ((GFF3DataEntry) entry).getAttribute("ID"));
            assertEquals("EDEN", ((GFF3DataEntry) entry).getAttribute("Name"));
            // Skip 9 entries
            for (int i = 0; i < 9; i++)
                reader.readNextEntry();
            entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3DataEntry);
            assertEquals("SO:0000316", ((GFF3DataEntry) entry).getTypeSOId());
            assertEquals("CDS", ((GFF3DataEntry) entry).getTypeSOName());
            assertEquals(1201L, ((GFF3DataEntry) entry).getStart());
            assertEquals(1500L, ((GFF3DataEntry) entry).getEnd());
            assertEquals(0, ((GFF3DataEntry) entry).getPhase());
            assertEquals("mRNA00001", ((GFF3DataEntry) entry).getAttribute("Parent"));
            assertEquals("cds00001", ((GFF3DataEntry) entry).getAttribute("ID"));
            assertEquals("edenprotein.1", ((GFF3DataEntry) entry).getAttribute("Name"));
        }
    }

    @Test
    void testReadingWithFasta() throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("test_gff3_with_fasta.gff")) {
            final GFF3Reader reader = new GFF3Reader(stream, "UTF-8");
            GFF3Entry entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3PragmaEntry);
            entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3PragmaEntry);
            entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3DataEntry);
            entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3DataEntry);
            entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3FastaEntry);
            assertEquals("ctg123", ((GFF3FastaEntry) entry).getTag());
            assertEquals("cttctgggcgtacccgattctcggagaacttgccgcaccattccgccttgtgttcattgctgcctg",
                         ((GFF3FastaEntry) entry).getSequence());
            entry = reader.readNextEntry();
            assertTrue(entry instanceof GFF3FastaEntry);
            assertEquals("cnda0123", ((GFF3FastaEntry) entry).getTag());
            assertEquals("ttcaagtgctcagtcaatgtgattc", ((GFF3FastaEntry) entry).getSequence());
        }
    }
}
