package de.unibi.agbi.biodwh2.kegg.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.AnonymousFTPClient;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KeggUpdater extends Updater {
    private static final String FTPBasePath = "pub/kegg/medicus/";

    @Override
    public Version getNewestVersion() throws UpdaterException {
        AnonymousFTPClient ftpClient = connectToFTP();
        List<LocalDateTime> folderDateTimes = new ArrayList<>();
        folderDateTimes.add(ftpClient.getModificationTimeFromServer(FTPBasePath + "dgroup/dgroup"));
        folderDateTimes.add(ftpClient.getModificationTimeFromServer(FTPBasePath + "disease/disease"));
        folderDateTimes.add(ftpClient.getModificationTimeFromServer(FTPBasePath + "drug/drug"));
        folderDateTimes.add(ftpClient.getModificationTimeFromServer(FTPBasePath + "network/network"));
        ftpClient.tryDisconnect();
        Version newestVersion = null;
        for (LocalDateTime dateTime : folderDateTimes) {
            Version dateTimeVersion = dateTime != null ? convertDateTimeToVersion(dateTime) : null;
            if (dateTimeVersion != null && dateTimeVersion.compareTo(newestVersion) >= 0)
                newestVersion = dateTimeVersion;
        }
        return newestVersion;
    }

    private AnonymousFTPClient connectToFTP() throws UpdaterConnectionException {
        AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected;
        try {
            isConnected = ftpClient.connect("ftp.genome.jp");
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        if (!isConnected)
            throw new UpdaterConnectionException();
        return ftpClient;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) throws UpdaterException {
        AnonymousFTPClient ftpClient = connectToFTP();
        boolean success = updateFile(workspace, dataSource, ftpClient, "dgroup/dgroup");
        success = success && updateFile(workspace, dataSource, ftpClient, "disease/disease");
        success = success && updateFile(workspace, dataSource, ftpClient, "drug/drug");
        success = success && updateFile(workspace, dataSource, ftpClient, "network/network");
        success = success && updateFile(workspace, dataSource, ftpClient, "network/variant");
        return success;
    }

    private boolean updateFile(Workspace workspace, DataSource dataSource, AnonymousFTPClient ftpClient,
                               String filePath) throws UpdaterException {
        String fileName = Paths.get(filePath).getFileName().toString();
        try {
            String sourceFilePath = dataSource.resolveSourceFilePath(workspace, fileName);
            return ftpClient.downloadFile(FTPBasePath + filePath, sourceFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + fileName + "'", e);
        }
    }
}
