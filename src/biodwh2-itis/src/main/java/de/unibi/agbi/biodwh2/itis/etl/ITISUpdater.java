package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;

public class ITISUpdater extends Updater<ITISDataSource> {
    public ITISUpdater(ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace) throws UpdaterException {
        return false;
    }
}
