package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class MultiFileFTPWebUpdater<D extends DataSource> extends Updater<D> {
    private final HTTPFTPClient client;

    public MultiFileFTPWebUpdater(final D dataSource) {
        super(dataSource);
        client = new HTTPFTPClient(getFTPIndexUrl());
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        try {
            Version latestVersion = null;
            for (final String filePath : getFilePaths()) {
                final Version fileVersion = getNewestVersionFromFilePath(Paths.get(filePath));
                if (latestVersion == null || (fileVersion != null && fileVersion.compareTo(latestVersion) > 0))
                    latestVersion = fileVersion;
            }
            return latestVersion;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private Version getNewestVersionFromFilePath(final Path filePath) throws IOException {
        final Path directoryPath = filePath.getParent();
        final HTTPFTPClient.Entry[] entries = directoryPath == null ? client.listDirectory() : client.listDirectory(
                directoryPath.toString());
        for (final HTTPFTPClient.Entry entry : entries)
            if (entry.name.equals(filePath.getFileName().toString()))
                return getVersionForFileName(entry);
        return null;
    }

    private static Version getVersionForFileName(final HTTPFTPClient.Entry entry) {
        final String date = StringUtils.split(entry.modificationDate, " ", 2)[0];
        final String[] dateParts = StringUtils.split(date, '-');
        return new Version(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                           Integer.parseInt(dateParts[2]));
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            for (final String fileName : getFilePaths()) {
                final String localFileName = Paths.get(fileName).getFileName().toString();
                final String resolvedFilePath = dataSource.resolveSourceFilePath(workspace, localFileName);
                HTTPClient.downloadFileAsBrowser(getFTPIndexUrl() + fileName, resolvedFilePath);
            }
            return true;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    protected abstract String getFTPIndexUrl();

    protected abstract String[] getFilePaths();
}
