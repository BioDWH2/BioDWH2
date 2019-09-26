package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.AnonymousFTPClient;

import java.io.IOException;
import java.time.LocalDateTime;

public class NDFRTUpdater extends Updater {
    private static final String FtpFilePath = "pub/cacore/EVS/NDF-RT/NDFRT_Public_All.zip";

    @Override
    public Version getNewestVersion() {
        AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected = ftpClient.tryConnect("ftp1.nci.nih.gov");
        if (!isConnected)
            return null;
        LocalDateTime dateTime = ftpClient.getModificationTimeFromServer(FtpFilePath);
        return dateTime != null ? convertDateTimeToVersion(dateTime) : null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) {
        AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected = ftpClient.tryConnect("ftp1.nci.nih.gov");
        if (!isConnected)
            return false;
        boolean success;
        try {
            String sourceFilePath = dataSource.resolveSourceFilePath(workspace, "NDFRT_Public_All.zip");
            success = ftpClient.downloadFile(FtpFilePath, sourceFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        ftpClient.tryDisconnect();
        return success;
    }
}
