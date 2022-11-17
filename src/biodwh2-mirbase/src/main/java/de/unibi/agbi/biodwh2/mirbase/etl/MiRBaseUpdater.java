package de.unibi.agbi.biodwh2.mirbase.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.mirbase.MiRBaseDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MiRBaseUpdater extends MultiFileFTPWebUpdater<MiRBaseDataSource> {
    public MiRBaseUpdater(final MiRBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://www.mirbase.org/ftp/CURRENT/";
    }

    @Override
    protected String[] getFilePaths() {
        final List<String> fileNames = new ArrayList<>(
                Arrays.asList("hairpin.fa.gz", "mature.fa.gz", "miRNA.dat.gz", "miRNA.str.gz"));
        try {
            final HTTPFTPClient.Entry[] dbEntries = client.listDirectory("database_files");
            for (final HTTPFTPClient.Entry entry : dbEntries) {
                if (entry.name.endsWith(".gz"))
                    fileNames.add("database_files/" + entry.name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileNames.toArray(new String[0]);
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{"hairpin.fa.gz", "mature.fa.gz", "miRNA.dat.gz", "miRNA.str.gz"};
    }
}
