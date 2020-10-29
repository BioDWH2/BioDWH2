package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;

public class DisGeNetUpdater extends Updater<DisGeNetDataSource> {
    public DisGeNetUpdater(final DisGeNetDataSource dataSource) {
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
