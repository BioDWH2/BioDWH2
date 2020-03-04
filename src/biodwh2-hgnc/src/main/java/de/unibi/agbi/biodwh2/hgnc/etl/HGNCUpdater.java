package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileFTPUpdater;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;

public class HGNCUpdater extends SingleFileFTPUpdater<HGNCDataSource> {
    @Override
    protected String getFTPAddress() {
        return "ftp.ebi.ac.uk";
    }

    @Override
    protected String getFTPFilePath() {
        return "pub/databases/genenames/new/tsv/hgnc_complete_set.txt";
    }

    @Override
    protected String getTargetFileName() {
        return "hgnc_complete_set.txt";
    }
}
