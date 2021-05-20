package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.ndfrt.NDFRTDataSource;

public class NDFRTUpdater extends MultiFileFTPWebUpdater<NDFRTDataSource> {
    public NDFRTUpdater(NDFRTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://evs.nci.nih.gov/ftp1/NDF-RT/Archive/";
    }

    @Override
    protected String[] getFilePaths() {
        return new String[]{"NDFRT_Public_All 2018-02-05.zip"};
    }
}
