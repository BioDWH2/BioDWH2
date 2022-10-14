package de.unibi.agbi.biodwh2.tarbase.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterConnectionException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterOnlyManuallyException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.core.net.HTTPClient;
import de.unibi.agbi.biodwh2.tarbase.TarBaseDataSource;

import java.io.IOException;

public class TarBaseUpdater extends Updater<TarBaseDataSource> {
    static final String FILE_NAME = "tarbase_data.tar.gz";

    public TarBaseUpdater(final TarBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected Version getNewestVersion() {
        return new Version(8, 0);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        final String downloadUrl = dataSource.getProperties(workspace).get("downloadUrl");
        if (downloadUrl == null) {
            throw new UpdaterOnlyManuallyException("Please provide a valid downloadUrl data source property");
        }
        try {
            HTTPClient.downloadFileAsBrowser(downloadUrl, dataSource.resolveSourceFilePath(workspace, FILE_NAME));
        } catch (IOException e) {
            throw new UpdaterConnectionException("Failed to download file '" + FILE_NAME + "'", e);
        }
        return true;
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{FILE_NAME};
    }
}
