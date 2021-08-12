package de.unibi.agbi.biodwh2.usdaplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.usdaplants.USDAPlantsDataSource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class USDAPlantsUpdater extends Updater<USDAPlantsDataSource> {
    private static final String DOWNLOAD_URL = "https://plants.sc.egov.usda.gov/assets/docs/CompletePLANTSList/plantlst.txt";
    static final String PLANT_LIST_FILE_NAME = "plantlst.txt";

    public USDAPlantsUpdater(final USDAPlantsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() {
        return null;
    }

    @Override
    protected boolean versionNotAvailable() {
        return true;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final File newFile = new File(dataSource.resolveSourceFilePath(workspace, PLANT_LIST_FILE_NAME));
        try {
            FileUtils.copyURLToFile(new URL(DOWNLOAD_URL), newFile);
            return true;
        } catch (IOException e) {
            throw new UpdaterConnectionException(e);
        }
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{PLANT_LIST_FILE_NAME};
    }
}
