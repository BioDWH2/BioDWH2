package de.unibi.agbi.biodwh2.herb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.herb.HerbDataSource;

public class HerbUpdater extends Updater<HerbDataSource> {
    private static final String DOWNLOAD_URL_PREFIX = "http://herb.ac.cn/download/file/?file_path=/data/Web_server/HERB_web/static/download_data/";
    static final String DISEASES_FILE_NAME = "HERB_disease_info.txt";
    static final String EXPERIMENTS_FILE_NAME = "HERB_experiment_info.txt";
    static final String HERBS_FILE_NAME = "HERB_herb_info.txt";
    static final String INGREDIENTS_FILE_NAME = "HERB_ingredient_info.txt";
    static final String REFERENCES_FILE_NAME = "HERB_reference_info.txt";
    static final String TARGETS_FILE_NAME = "HERB_target_info.txt";

    public HerbUpdater(final HerbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : expectedFileNames())
            downloadFileAsBrowser(workspace, DOWNLOAD_URL_PREFIX + fileName, fileName);
        return true;
    }

    @Override
    protected boolean versionNotAvailable() {
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                DISEASES_FILE_NAME, EXPERIMENTS_FILE_NAME, HERBS_FILE_NAME, INGREDIENTS_FILE_NAME, REFERENCES_FILE_NAME,
                TARGETS_FILE_NAME
        };
    }
}
