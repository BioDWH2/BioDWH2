package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public abstract class OBOOntologyUpdater<D extends DataSource> extends Updater<D> {
    public OBOOntologyUpdater(final D dataSource) {
        super(dataSource);
    }

    @Override
    public final Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            return getVersionFromDownloadFile();
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version number", e);
        }
    }

    private Version getVersionFromDownloadFile() throws IOException {
        String line;
        try (InputStream stream = HTTPClient.getUrlInputStream(getDownloadUrl());
             BufferedReader bufferedReader = FileUtils.createBufferedReaderFromStream(stream)) {
            line = bufferedReader.readLine();
            while (line != null && !line.trim().startsWith("data-version:"))
                line = bufferedReader.readLine();
        }
        return line == null ? null : getVersionFromDataVersionLine(line);
    }

    protected abstract String getDownloadUrl();

    protected abstract Version getVersionFromDataVersionLine(final String dataVersion);

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            final String targetFilePath = dataSource.resolveSourceFilePath(workspace, getTargetFileName());
            HTTPClient.downloadFileAsBrowser(getDownloadUrl(), targetFilePath);
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download '" + getTargetFileName() + "'", e);
        }
        return true;
    }

    protected abstract String getTargetFileName();
}
