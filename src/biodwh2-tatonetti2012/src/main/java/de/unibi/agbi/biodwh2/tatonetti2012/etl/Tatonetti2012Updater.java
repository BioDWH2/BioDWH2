package de.unibi.agbi.biodwh2.tatonetti2012.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.tatonetti2012.Tatonetti2012DataSource;

public class Tatonetti2012Updater extends Updater<Tatonetti2012DataSource> {
    private static final String OFFSIDES_DOWNLOAD_URL = "https://stacks.stanford.edu/file/druid:zq918jm7358/3003377s-offsides.zip";
    private static final String TWOSIDES_DOWNLOAD_URL = "https://stacks.stanford.edu/file/druid:zq918jm7358/3003377s-twosides.zip";
    static final String OFFSIDES_FILE_NAME = "offsides.zip";
    static final String TWOSIDES_FILE_NAME = "twosides.zip";

    public Tatonetti2012Updater(final Tatonetti2012DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) {
        return new Version(2012, 3, 14);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, OFFSIDES_DOWNLOAD_URL, OFFSIDES_FILE_NAME);
        downloadFileAsBrowser(workspace, TWOSIDES_DOWNLOAD_URL, TWOSIDES_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{OFFSIDES_FILE_NAME, TWOSIDES_FILE_NAME};
    }
}
