package de.unibi.agbi.biodwh2.core.io.gmt;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class GMTReaderTest {
    @Test
    void testReadingFromFileStream() throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream("test_gmt.gmt")) {
            final GMTReader reader = new GMTReader(stream, StandardCharsets.UTF_8);
            GeneSet entry = reader.readNextEntry();
            assertNotNull(entry);
            assertEquals("http://pathwaycommons.org/pc12/Pathway_b1a2d04e7635bad8d582ed1eab55e2d7", entry.getName());
            assertEquals("name: alanine degradation III; datasource: humancyc; organism: 9606; idtype: hgnc symbol",
                         entry.getDescription());
            assertArrayEquals(new String[]{"GPT", "GPT2"}, entry.getGenes());
            entry = reader.readNextEntry();
            assertNotNull(entry);
            assertEquals("http://pathwaycommons.org/pc12/Pathway_b1c9b1210c7370746021f526d50b9063", entry.getName());
            assertEquals(
                    "name: Validated transcriptional targets of TAp63 isoforms; datasource: pid; organism: 9606; idtype: hgnc symbol",
                    entry.getDescription());
            assertArrayEquals(new String[]{
                    "ABL1", "ADA", "AEN", "BAX", "BBC3", "BTRC", "CABLES1", "CDKN1A", "CDKN2A", "CHUK", "CLCA2",
                    "DHRS3", "DICER1", "DST", "EGR2", "EP300", "EVPL", "FAS", "FDXR", "FLOT2", "GADD45A", "GDF15",
                    "GPX2", "HBP1", "IGFBP3", "IKBKB", "ITCH", "ITGA3", "ITGB4", "JAG1", "MDM2", "MFGE8", "NOC2L",
                    "NQO1", "OGG1", "PERP", "PLK1", "PMAIP1", "PML", "PRKCD", "S100A2", "SERPINB5", "SHH", "SMARCD3",
                    "SP1", "SPATA18", "SSRP1", "TFAP2C", "TP53I3", "TP63", "TRAF4", "VDR", "WWP1", "YWHAQ"
            }, entry.getGenes());
            entry = reader.readNextEntry();
            assertNotNull(entry);
            assertEquals("http://pathwaycommons.org/pc12/Pathway_b1ddc18b646866adf7569b333ab8215a", entry.getName());
            assertEquals(
                    "name: superpathway of geranylgeranyldiphosphate biosynthesis I (via mevalonate); datasource: humancyc; organism: 9606; idtype: hgnc symbol",
                    entry.getDescription());
            assertArrayEquals(new String[]{
                    "ACAA1", "ACAT1", "ACAT2", "FDPS", "GGPS1", "HADHB", "HMGCR", "HMGCS1", "HMGCS2", "IDI1", "IDI2",
                    "MVD", "MVK", "PMVK"
            }, entry.getGenes());
            entry = reader.readNextEntry();
            assertNull(entry);
        }
    }
}