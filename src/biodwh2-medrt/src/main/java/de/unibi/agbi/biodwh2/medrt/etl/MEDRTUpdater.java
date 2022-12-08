package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;

public class MEDRTUpdater extends MultiFileFTPWebUpdater<MEDRTDataSource> {
    static final String FILE_NAME = "Core_MEDRT_XML.zip";

    public MEDRTUpdater(MEDRTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://evs.nci.nih.gov/ftp1/MED-RT/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        return new String[]{FILE_NAME};
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
