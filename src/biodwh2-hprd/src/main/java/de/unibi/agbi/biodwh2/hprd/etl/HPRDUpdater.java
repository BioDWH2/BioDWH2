package de.unibi.agbi.biodwh2.hprd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.hprd.HPRDDataSource;

public class HPRDUpdater extends Updater<HPRDDataSource> {
    //private static final String VERSION_URL = "http://hprd.org/download";
    //private static final Pattern VERSION_PATTERN = Pattern.compile("HPRD_Release(\\d+)(_\\d+\\.tar\\.gz)");
    private static final String ARCHIVE_ORG_DOWNLOAD_URL = "https://web.archive.org/web/20230328073021if_/http://www.hprd.org/RELEASE9/HPRD_FLAT_FILES_041310.tar.gz";
    static final String FILE_NAME = "HPRD_FLAT_FILES.tar.gz";

    public HPRDUpdater(final HPRDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) {
        return new Version(9, 0);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, ARCHIVE_ORG_DOWNLOAD_URL, FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
