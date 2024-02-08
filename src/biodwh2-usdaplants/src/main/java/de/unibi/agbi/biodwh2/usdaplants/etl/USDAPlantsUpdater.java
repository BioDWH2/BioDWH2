package de.unibi.agbi.biodwh2.usdaplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.usdaplants.USDAPlantsDataSource;

public class USDAPlantsUpdater extends Updater<USDAPlantsDataSource> {
    private static final String DOWNLOAD_URL = "https://plants.usda.gov/assets/docs/CompletePLANTSList/plantlst.txt";
    static final String PLANT_LIST_FILE_NAME = "plantlst.txt";

    public USDAPlantsUpdater(final USDAPlantsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) {
        return null;
    }

    @Override
    protected boolean versionNotAvailable() {
        return true;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        downloadFileAsBrowser(workspace, DOWNLOAD_URL, PLANT_LIST_FILE_NAME);
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{PLANT_LIST_FILE_NAME};
    }
}
