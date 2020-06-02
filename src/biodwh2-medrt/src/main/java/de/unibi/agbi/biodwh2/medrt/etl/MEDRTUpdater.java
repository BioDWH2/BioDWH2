package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.medrt.MEDRTDataSource;

public class MEDRTUpdater extends MultiFileFTPWebUpdater<MEDRTDataSource> {
    @Override
    protected String getFTPIndexUrl() {
        return "https://evs.nci.nih.gov/ftp1/MED-RT/";
    }

    @Override
    protected String[] getFilePaths() {
        return new String[]{"Core_MEDRT_XML.zip"};
    }
}
