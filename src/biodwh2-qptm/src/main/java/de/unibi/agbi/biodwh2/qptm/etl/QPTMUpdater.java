package de.unibi.agbi.biodwh2.qptm.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.qptm.QPTMDataSource;

import java.io.IOException;

public class QPTMUpdater extends Updater<QPTMDataSource> {
    static final String FILE_NAME = "qPTM_all_data.zip";

    public QPTMUpdater(final QPTMDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        final String downloadUrl = dataSource.getStringProperty(workspace, "downloadUrl");
        if (downloadUrl == null)
            throw new UpdaterException("Missing download url data source configuration");
        try {
            final var dateTime = HTTPClient.peekZipModificationDateTime(downloadUrl);
            if (dateTime != null)
                return new Version(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to retrieve version", e);
        }
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String downloadUrl = dataSource.getStringProperty(workspace, "downloadUrl");
        if (downloadUrl == null)
            throw new UpdaterException("Missing download url data source configuration");
        downloadFileAsBrowser(workspace, downloadUrl, FILE_NAME);
        return true;
    }
}
