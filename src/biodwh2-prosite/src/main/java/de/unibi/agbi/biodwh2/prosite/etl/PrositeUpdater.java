package de.unibi.agbi.biodwh2.prosite.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPUpdater;
import de.unibi.agbi.biodwh2.prosite.PrositeDataSource;

public class PrositeUpdater extends MultiFileFTPUpdater<PrositeDataSource> {
    static final String PROSITE_FILE_NAME = "prosite.dat";
    static final String PRORULE_FILE_NAME = "prorule.dat";
    static final String DOC_FILE_NAME = "prosite.doc";

    public PrositeUpdater(final PrositeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPAddress() {
        return "ftp.expasy.org";
    }

    @Override
    protected String[] getFTPFilePaths() {
        return new String[]{
                "/databases/prosite/" + PROSITE_FILE_NAME, "/databases/prosite/" + PRORULE_FILE_NAME,
                "/databases/prosite/" + DOC_FILE_NAME
        };
    }

    @Override
    protected String[] getTargetFileNames() {
        return new String[]{PROSITE_FILE_NAME, PRORULE_FILE_NAME, DOC_FILE_NAME};
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{PROSITE_FILE_NAME, PRORULE_FILE_NAME, DOC_FILE_NAME};
    }

}
