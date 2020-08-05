package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPUpdater;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;

public class HGNCUpdater extends MultiFileFTPUpdater<HGNCDataSource> {
    public HGNCUpdater(HGNCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPAddress() {
        return "ftp.ebi.ac.uk";
    }

    @Override
    protected String[] getFTPFilePaths() {
        return new String[]{"pub/databases/genenames/new/tsv/hgnc_complete_set.txt"};
    }

    @Override
    protected String[] getTargetFileNames() {
        return new String[]{"hgnc_complete_set.txt"};
    }
}
