package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.AnonymousFTPClient;

import java.io.IOException;
import java.time.LocalDateTime;

public class HGNCUpdater extends Updater {
    private static final String FtpFilePath = "pub/databases/genenames/new/tsv/hgnc_complete_set.txt";

    @Override
    public Version getNewestVersion() {
        AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected = ftpClient.tryConnect("ftp.ebi.ac.uk");
        if (!isConnected)
            return null;
        LocalDateTime dateTime = ftpClient.getModificationTimeFromServer(FtpFilePath);
        ftpClient.tryDisconnect();
        return dateTime != null ? convertDateTimeToVersion(dateTime) : null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) {
        AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected = ftpClient.tryConnect("ftp.ebi.ac.uk");
        if (!isConnected)
            return false;
        boolean success;
        try {
            String sourceFilePath = dataSource.resolveSourceFilePath(workspace, "hgnc_complete_set.txt");
            success = ftpClient.downloadFile(FtpFilePath, sourceFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        ftpClient.tryDisconnect();
        return success;
    }
}
