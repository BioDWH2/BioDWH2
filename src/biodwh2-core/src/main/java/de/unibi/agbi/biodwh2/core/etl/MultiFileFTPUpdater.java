package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.AnonymousFTPClient;

import java.io.IOException;
import java.time.LocalDateTime;

public abstract class MultiFileFTPUpdater<D extends DataSource> extends Updater<D> {
    @Override
    public final Version getNewestVersion() throws UpdaterException {
        AnonymousFTPClient ftpClient = connectToFTP();
        Version latestVersion = null;
        for (String filePath : getFTPFilePaths()) {
            LocalDateTime dateTime = ftpClient.getModificationTimeFromServer(filePath);
            Version fileVersion = convertDateTimeToVersion(dateTime);
            if (latestVersion == null || fileVersion.compareTo(latestVersion) > 0)
                latestVersion = fileVersion;
        }
        ftpClient.tryDisconnect();
        return latestVersion;
    }

    private AnonymousFTPClient connectToFTP() throws UpdaterConnectionException {
        AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected;
        try {
            isConnected = ftpClient.connect(getFTPAddress());
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        if (!isConnected)
            throw new UpdaterConnectionException();
        return ftpClient;
    }

    protected abstract String getFTPAddress();

    protected abstract String[] getFTPFilePaths();

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, D dataSource) throws UpdaterException {
        AnonymousFTPClient ftpClient = connectToFTP();
        boolean success = true;
        for (int i = 0; i < getFTPFilePaths().length; i++) {
            try {
                String sourceFilePath = dataSource.resolveSourceFilePath(workspace, getTargetFileNames()[i]);
                success = success && ftpClient.downloadFile(getFTPFilePaths()[i], sourceFilePath);
            } catch (IOException e) {
                throw new UpdaterConnectionException("Failed to download '" + getTargetFileNames()[i] + "'", e);
            }
        }
        ftpClient.tryDisconnect();
        return success;
    }

    protected abstract String[] getTargetFileNames();
}
