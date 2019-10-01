package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileFTPUpdater;

public class NDFRTUpdater extends SingleFileFTPUpdater {
    @Override
    protected String getFTPAddress() {
        return "ftp1.nci.nih.gov";
    }

    @Override
    protected String getFTPFilePath() {
        return "pub/cacore/EVS/NDF-RT/NDFRT_Public_All.zip";
    }

    @Override
    protected String getTargetFileName() {
        return "NDFRT_Public_All.zip";
    }
}
