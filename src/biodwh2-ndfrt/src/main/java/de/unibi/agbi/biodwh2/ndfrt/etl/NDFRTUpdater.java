package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.ndfrt.NDFRTDataSource;

public class NDFRTUpdater extends SingleFileFTPWebUpdater<NDFRTDataSource> {
    @Override
    protected String getFTPIndexUrl() {
        return "https://evs.nci.nih.gov/ftp1/NDF-RT/";
    }

    @Override
    protected String getFileName() {
        return "NDFRT_Public_All.zip";
    }
}
