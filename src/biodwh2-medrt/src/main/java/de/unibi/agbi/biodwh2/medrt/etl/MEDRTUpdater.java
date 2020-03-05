package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;

public class MEDRTUpdater extends SingleFileFTPWebUpdater<MEDRTDataSource> {
    @Override
    protected String getFTPIndexUrl() {
        return "https://evs.nci.nih.gov/ftp1/MED-RT/";
    }

    @Override
    protected String getFileName() {
        return "Core_MEDRT_XML.zip";
    }
}
