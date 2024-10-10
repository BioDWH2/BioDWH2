package de.unibi.agbi.biodwh2.qptmplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.qptmplants.QPTMPlantsDataSource;

import java.io.IOException;

public class QPTMPlantsUpdater extends Updater<QPTMPlantsDataSource> {
    static final String FILE_NAME = "qPTMplants_all_data.zip";

    public QPTMPlantsUpdater(final QPTMPlantsDataSource dataSource) {
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
