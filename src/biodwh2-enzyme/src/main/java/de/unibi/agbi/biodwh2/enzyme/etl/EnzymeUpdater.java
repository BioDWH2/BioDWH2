package de.unibi.agbi.biodwh2.enzyme.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPUpdater;
import de.unibi.agbi.biodwh2.enzyme.EnzymeDataSource;

public class EnzymeUpdater extends MultiFileFTPUpdater<EnzymeDataSource> {
    static final String FILE_NAME = "enzyme.dat";

    public EnzymeUpdater(final EnzymeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPAddress() {
        return "ftp.expasy.org";
    }

    @Override
    protected String[] getFTPFilePaths() {
        return new String[]{"/databases/enzyme/" + FILE_NAME};
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
