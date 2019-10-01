package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileFTPUpdater;

public class MEDRTUpdater extends SingleFileFTPUpdater {
    @Override
    protected String getFTPAddress() {
        return "ftp1.nci.nih.gov";
    }

    @Override
    protected String getFTPFilePath() {
        return "pub/cacore/EVS/MED-RT/Core_MEDRT_XML.zip";
    }

    @Override
    protected String getTargetFileName() {
        return "Core_MEDRT_XML.zip";
    }
}
