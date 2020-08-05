package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

public class HPOUpdater extends Updater<HPODataSource> {
    public HPOUpdater(final HPODataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion() {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) {
        return false;
    }
}
