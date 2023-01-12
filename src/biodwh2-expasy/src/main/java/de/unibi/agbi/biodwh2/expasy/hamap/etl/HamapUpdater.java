package de.unibi.agbi.biodwh2.expasy.hamap.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPUpdater;
import de.unibi.agbi.biodwh2.expasy.hamap.HamapDataSource;

public class HamapUpdater extends MultiFileFTPUpdater<HamapDataSource> {
    static final String HAMAP_FILE_NAME = "hamap_rules.dat";

    public HamapUpdater(final HamapDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPAddress() {
        return "ftp.expasy.org";
    }

    @Override
    protected String[] getFTPFilePaths() {
        return new String[]{
                "/databases/hamap/" + HAMAP_FILE_NAME
        };
    }

    @Override
    protected String[] getTargetFileNames() {
        return new String[]{HAMAP_FILE_NAME};
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{HAMAP_FILE_NAME};
    }
}
