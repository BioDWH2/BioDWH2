package de.unibi.agbi.biodwh2.pathwaycommons.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.pathwaycommons.PathwayCommonsDataSource;

public class PathwayCommonsUpdater extends Updater<PathwayCommonsDataSource> {
    public PathwayCommonsUpdater(final PathwayCommonsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() throws UpdaterException {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        return false;
    }
}
