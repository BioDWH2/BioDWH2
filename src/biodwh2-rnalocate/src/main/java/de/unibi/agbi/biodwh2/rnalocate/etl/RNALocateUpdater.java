package de.unibi.agbi.biodwh2.rnalocate.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.rnalocate.RNALocateDataSource;

import java.io.IOException;
import java.time.LocalDateTime;

public class RNALocateUpdater extends Updater<RNALocateDataSource> {
    private static final String EXPERIMENTAL_FILE_URL = "http://www.rna-society.org/rnalocate/download/All%20experimental%20RNA%20subcellular%20localization%20data.zip";
    private static final String DATABASE_FILE_URL = "http://www.rna-society.org/rnalocate/download/Other%20RNA%20subcellular%20localization%20data.zip";
    static final String EXPERIMENTAL_FILE_NAME = "All experimental RNA subcellular localization data.zip";
    static final String DATABASE_FILE_NAME = "Other RNA subcellular localization data.zip";

    public RNALocateUpdater(final RNALocateDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        try {
            final LocalDateTime dateTimeExperimental = HTTPClient.peekZipModificationDateTime(EXPERIMENTAL_FILE_URL);
            final LocalDateTime dateTimeDatabase = HTTPClient.peekZipModificationDateTime(DATABASE_FILE_URL);
            if (dateTimeExperimental == null && dateTimeDatabase == null)
                return null;
            if (dateTimeExperimental == null)
                return createVersionFromDateTime(dateTimeDatabase);
            if (dateTimeDatabase == null)
                return createVersionFromDateTime(dateTimeExperimental);
            if (dateTimeExperimental.isAfter(dateTimeDatabase))
                return createVersionFromDateTime(dateTimeExperimental);
            return createVersionFromDateTime(dateTimeDatabase);
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    private Version createVersionFromDateTime(final LocalDateTime dateTime) {
        return new Version(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(EXPERIMENTAL_FILE_URL,
                                             dataSource.resolveSourceFilePath(workspace, EXPERIMENTAL_FILE_NAME));
            HTTPClient.downloadFileAsBrowser(DATABASE_FILE_URL,
                                             dataSource.resolveSourceFilePath(workspace, DATABASE_FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{EXPERIMENTAL_FILE_NAME, DATABASE_FILE_NAME};
    }
}
