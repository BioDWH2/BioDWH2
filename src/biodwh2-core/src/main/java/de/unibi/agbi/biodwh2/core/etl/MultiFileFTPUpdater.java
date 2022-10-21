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
    public MultiFileFTPUpdater(final D dataSource) {
        super(dataSource);
    }

    @Override
    public final Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final AnonymousFTPClient ftpClient = connectToFTP();
        Version latestVersion = null;
        for (final String filePath : getFTPFilePaths()) {
            final LocalDateTime dateTime = ftpClient.getModificationTimeFromServer(filePath);
            final Version fileVersion = convertDateTimeToVersion(dateTime);
            if (latestVersion == null || fileVersion.compareTo(latestVersion) > 0)
                latestVersion = fileVersion;
        }
        ftpClient.tryDisconnect();
        return latestVersion;
    }

    private AnonymousFTPClient connectToFTP() throws UpdaterConnectionException {
        final AnonymousFTPClient ftpClient = new AnonymousFTPClient();
        boolean isConnected;
        try {
            isConnected = ftpClient.connect(getFTPAddress());
        } catch (IOException e) {
            throw new UpdaterConnectionException(getFTPAddress(), e);
        }
        if (!isConnected)
            throw new UpdaterConnectionException();
        return ftpClient;
    }

    protected abstract String getFTPAddress();

    protected abstract String[] getFTPFilePaths();

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        final AnonymousFTPClient ftpClient = connectToFTP();
        boolean success = true;
        for (int i = 0; i < getFTPFilePaths().length; i++) {
            try {
                final String sourceFilePath = dataSource.resolveSourceFilePath(workspace, getTargetFileNames()[i]);
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
