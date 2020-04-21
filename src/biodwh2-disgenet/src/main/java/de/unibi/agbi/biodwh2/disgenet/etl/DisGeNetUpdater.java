package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;

public class DisGeNetUpdater extends Updater<DisGeNetDataSource> {
    @Override
    public Version getNewestVersion() throws UpdaterException {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, DisGeNetDataSource dataSource) throws UpdaterException {
        return false;
    }
}
