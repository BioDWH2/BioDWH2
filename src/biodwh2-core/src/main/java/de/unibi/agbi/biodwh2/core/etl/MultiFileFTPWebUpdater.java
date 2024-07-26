package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPFTPClient;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public abstract class MultiFileFTPWebUpdater<D extends DataSource> extends Updater<D> {
    protected final HTTPFTPClient client;

    public MultiFileFTPWebUpdater(final D dataSource) {
        super(dataSource);
        client = new HTTPFTPClient(getFTPIndexUrl());
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            Version latestVersion = null;
            for (final String filePath : getFilePaths(workspace)) {
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
                directoryPath.toString().replace("\\", "/"));
        for (final HTTPFTPClient.Entry entry : entries)
            if (entry.name.equals(filePath.getFileName().toString()))
                return getVersionForEntry(entry);
        return null;
    }

    public static Version getVersionForEntry(final HTTPFTPClient.Entry entry) {
        final String date = StringUtils.split(entry.modificationDate, " ", 2)[0];
        if (date.contains("-")) {
            final String[] dateParts = StringUtils.split(date, "-", 3);
            if (StringUtils.isNumeric(dateParts[1])) {
                return new Version(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]),
                                   Integer.parseInt(dateParts[2]));
            }
            final int monthNumber = TextUtils.threeLetterMonthNameToInt(dateParts[1].toLowerCase(Locale.US));
            if (monthNumber != -1) {
                return new Version(Integer.parseInt(dateParts[2]), monthNumber, Integer.parseInt(dateParts[0]));
            }
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : getFilePaths(workspace)) {
            final String localFileName = Paths.get(fileName).getFileName().toString();
            downloadFileAsBrowser(workspace, getFTPIndexUrl() + fileName, localFileName);
        }
        return true;
    }

    protected abstract String getFTPIndexUrl();

    protected abstract String[] getFilePaths(final Workspace workspace) throws UpdaterException;
}
