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
        assertEquals("2020-05-25 13:30", entries[0].modificationDate);
        assertEquals("320M", entries[0].size);
    }
}