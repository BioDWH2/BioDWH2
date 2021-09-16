package de.unibi.agbi.biodwh2.core.net;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HTTPFTPClientTest {
    @Test
    void parseWebSourceWithTable() throws IOException {
        String source = getSourceFromResources("test_ftp_directory_table.html");
        HTTPFTPClient client = new HTTPFTPClient("https://evs.nci.nih.gov/");
        HTTPFTPClient.Entry[] entries = client.parseWebSource("ftp1/NDF-RT/", source);
        assertNotNull(entries);
        assertEquals(13, entries.length);
        assertEquals("77_diff_2018.02.05.17AB_bin.zip", entries[0].name);
        assertEquals("2018-02-05 13:00", entries[0].modificationDate);
        assertEquals("59K", entries[0].size);

    }

    private String getSourceFromResources(final String fileName) throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return FileUtils.readFileToString(file, "UTF-8");
    }

    @Test
    void parseWebSourceWithPre() throws IOException {
        String source = getSourceFromResources("test_ftp_directory_pre.html");
        HTTPFTPClient client = new HTTPFTPClient("https://ftp.ncbi.nih.gov/");
        HTTPFTPClient.Entry[] entries = client.parseWebSource("pubchem/Compound/CURRENT-Full/SDF", source);
        assertNotNull(entries);
        assertEquals(24, entries.length);
        assertEquals("Compound_000000001_000500000.sdf.gz", entries[0].name);
        assertEquals("https://ftp.ncbi.nih.gov/pubchem/Compound/CURRENT-Full/SDF/Compound_000000001_000500000.sdf.gz",
                     entries[0].fullUrl);
        assertEquals("2020-05-25 13:30", entries[0].modificationDate);
        assertEquals("320M", entries[0].size);
    }

    @Test
    void parseWebSourceWithPreTruncatedNames() throws IOException {
        String source = getSourceFromResources("test_ftp_directory_pre2.html");
        HTTPFTPClient client = new HTTPFTPClient("http://ftp.ebi.ac.uk/");
        HTTPFTPClient.Entry[] entries = client.parseWebSource(
                "pub/databases/opentargets/platform/latest/output/etl/json/molecule/", source);
        assertNotNull(entries);
        assertEquals(14, entries.length);
        assertEquals("_SUCCESS", entries[0].name);
        assertEquals("http://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/json/molecule/_SUCCESS",
                     entries[0].fullUrl);
        assertEquals("27-Jun-2021 14:05", entries[0].modificationDate);
        assertEquals("0", entries[0].size);
        assertEquals("part-00000-d5fcb613-37bb-4724-86c1-99e601f4fc2c-c000.json", entries[1].name);
        assertEquals(
                "http://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/json/molecule/part-00000-d5fcb613-37bb-4724-86c1-99e601f4fc2c-c000.json",
                entries[1].fullUrl);
        assertEquals("27-Jun-2021 14:05", entries[1].modificationDate);
        assertEquals("93466", entries[1].size);
    }
}