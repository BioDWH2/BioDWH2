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
                              "miRNA.dat.gz", "miRNA.str.gz", "miRNA.xls.gz", "miRNA_high_conf.dat.gz",
                              "organisms.txt.gz"));
        try {
            final HTTPFTPClient.Entry[] genomeEntries = client.listDirectory("genomes");
            for (HTTPFTPClient.Entry entry : genomeEntries) {
                if (entry.name.endsWith(".gff3"))
                    fileNames.add("genomes/" + entry.name);
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
                "miRNA.str.gz", "miRNA.xls.gz", "miRNA_high_conf.dat.gz", "organisms.txt.gz", "hsa.gff3"
        };
    }
}
