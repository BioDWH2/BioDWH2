package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.AnonymousFTPClient;

import java.time.LocalDateTime;

public class NDFRTUpdater extends Updater {
    public NDFRTUpdater(Workspace workspace) {
        super(workspace);
    }

    @Override
    public Version getNewestVersion() {
        String filePath = "ftp1/NDF-RT/NDFRT_Public_All.zip";
        AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected = ftpClient.tryConnect("evs.nci.nih.gov");
        if (!isConnected)
            return null;
        LocalDateTime dateTime = ftpClient.getModificationTimeFromServer(filePath);
        return dateTime != null ? convertDateTimeToVersion(dateTime) : null;
    }

    @Override
    protected boolean tryUpdateFiles(DataSource dataSource) {
        return false;
    }
}
