package de.unibi.agbi.biodwh2.herb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.herb.HerbDataSource;

import java.io.IOException;

public class HerbUpdater extends Updater<HerbDataSource> {
    private static final String URL_PREFIX = "http://herb.ac.cn/download/file/?file_path=/data/Web_server/HERB_web/static/download_data/";
    private static final String[] FILE_NAMES = new String[]{
            "HERB_disease_info.txt", "HERB_experiment_info.txt", "HERB_herb_info.txt", "HERB_ingredient_info.txt",
            "HERB_reference_info.txt", "HERB_target_info.txt"
    };

    public HerbUpdater(final HerbDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion(final Workspace workspace) {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        for (final String fileName : FILE_NAMES) {
            try {
                HTTPClient.downloadFileAsBrowser(URL_PREFIX + fileName,
                                                 dataSource.resolveSourceFilePath(workspace, fileName));
            } catch (IOException e) {
                throw new UpdaterConnectionException(e);
            }
        }
        return true;
    }

    @Override
    protected boolean versionNotAvailable() {
        return true;
    }
}
