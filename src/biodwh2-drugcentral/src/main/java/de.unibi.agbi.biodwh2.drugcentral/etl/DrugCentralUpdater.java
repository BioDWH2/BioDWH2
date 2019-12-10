package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;

public class DrugCentralUpdater extends Updater {
    @Override
    public Version getNewestVersion() throws UpdaterException {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, DataSource dataSource) throws UpdaterException {
        return false;
    }
}
