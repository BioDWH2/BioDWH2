package de.unibi.agbi.biodwh2.mondo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.mondo.MONDODataSource;

public class MONDOUpdater extends Updater<MONDODataSource> {
    @Override
    public Version getNewestVersion() throws UpdaterException {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, MONDODataSource dataSource) throws UpdaterException {
        return false;
    }
}
