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
                Arrays.asList("hairpin.fa.gz", "hairpin_high_conf.fa.gz", "mature.fa.gz", "mature_high_conf.fa.gz",
                              "miRNA.dat.gz", "miRNA.str.gz", "miRNA_high_conf.dat.gz"));
        try {
            final HTTPFTPClient.Entry[] genomeEntries = client.listDirectory("genomes");
            for (final HTTPFTPClient.Entry entry : genomeEntries) {
                if (entry.name.endsWith(".gff3"))
                    fileNames.add("genomes/" + entry.name);
            }
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
        return new String[]{
                "hairpin.fa.gz", "hairpin_high_conf.fa.gz", "mature.fa.gz", "mature_high_conf.fa.gz", "miRNA.dat.gz",
                "miRNA.str.gz", "miRNA_high_conf.dat.gz", "hsa.gff3"
        };
    }
}
