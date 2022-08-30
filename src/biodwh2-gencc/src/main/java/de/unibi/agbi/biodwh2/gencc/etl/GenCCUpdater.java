package de.unibi.agbi.biodwh2.gencc.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.gencc.GenCCDataSource;

import java.io.IOException;

public class GenCCUpdater extends Updater<GenCCDataSource> {
    private static final String DOWNLOAD_URL = "https://search.thegencc.org/download/action/submissions-export-tsv";
    public static final String FILE_NAME = "gencc-submissions.tsv";

    public GenCCUpdater(final GenCCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion() throws UpdaterException {
        // TODO: maybe once API is available
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        try {
            HTTPClient.downloadFileAsBrowser(DOWNLOAD_URL, dataSource.resolveSourceFilePath(workspace, FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }

    @Override
    protected boolean versionNotAvailable() {
        return true;
    }
}
