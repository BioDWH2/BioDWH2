package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.AnonymousFTPClient;

import java.io.IOException;
import java.time.LocalDateTime;

public abstract class SingleFileFTPUpdater extends Updater {
    @Override
    public final Version getNewestVersion() throws UpdaterException {
        AnonymousFTPClient ftpClient = connectToFTP();
        LocalDateTime dateTime = ftpClient.getModificationTimeFromServer(getFTPFilePath());
        ftpClient.tryDisconnect();
        return dateTime != null ? convertDateTimeToVersion(dateTime) : null;
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

    protected abstract String getFTPFilePath();

    @Override
    protected final boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) throws UpdaterException {
        AnonymousFTPClient ftpClient = connectToFTP();
        boolean success;
        try {
            String sourceFilePath = dataSource.resolveSourceFilePath(workspace, getTargetFileName());
            success = ftpClient.downloadFile(getFTPFilePath(), sourceFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + getTargetFileName() + "'", e);
        }
        ftpClient.tryDisconnect();
        return success;
    }

    protected abstract String getTargetFileName();
}
