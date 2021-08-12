package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPUpdater;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;

public class HGNCUpdater extends MultiFileFTPUpdater<HGNCDataSource> {
    static final String FILE_NAME = "hgnc_complete_set.txt";

    public HGNCUpdater(HGNCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPAddress() {
        return "ftp.ebi.ac.uk";
    }

    @Override
    protected String[] getFTPFilePaths() {
        return new String[]{"pub/databases/genenames/new/tsv/" + FILE_NAME};
    }

    @Override
    protected String[] getTargetFileNames() {
        return new String[]{FILE_NAME};
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
