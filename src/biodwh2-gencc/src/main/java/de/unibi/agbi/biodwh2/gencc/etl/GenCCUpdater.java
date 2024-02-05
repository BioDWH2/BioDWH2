package de.unibi.agbi.biodwh2.gencc.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.gencc.GenCCDataSource;

public class GenCCUpdater extends Updater<GenCCDataSource> {
    private static final String DOWNLOAD_URL = "https://search.thegencc.org/download/action/submissions-export-tsv";
    static final String FILE_NAME = "gencc-submissions.tsv";

    public GenCCUpdater(final GenCCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) {
        // TODO: maybe once API is available
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, FILE_NAME);
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
