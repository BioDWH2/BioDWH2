package de.unibi.agbi.biodwh2.themarker.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.themarker.TheMarkerDataSource;

public class TheMarkerUpdater extends Updater<TheMarkerDataSource> {
    private static final String DOWNLOAD_URL_PREFIX = "http://themarker.idrblab.cn/files/download/";
    static final String DRUGS_FILE_NAME = "drug_all.txt";
    static final String DRUGS_SDF_FILE_NAME = "drug_SDF_all.sdf";
    static final String DISEASES_FILE_NAME = "disease_all.txt";
    static final String MARKER_FILE_NAME = "marker_all.txt";
    static final String ASSOCIATIONS_FILE_NAME = "association_all.txt";

    public TheMarkerUpdater(final TheMarkerDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) throws UpdaterException {
        return null;
    }

    @Override
    protected boolean versionNotAvailable() {
        return true;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                DRUGS_FILE_NAME, DRUGS_SDF_FILE_NAME, DISEASES_FILE_NAME, MARKER_FILE_NAME, ASSOCIATIONS_FILE_NAME
        };
    }
}
