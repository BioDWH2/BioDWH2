package de.unibi.agbi.biodwh2.abdamed2.etl;

import de.unibi.agbi.biodwh2.abdamed2.ABDAMED2DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterOnlyManuallyException;
import de.unibi.agbi.biodwh2.core.model.Version;

public class ABDAMED2Updater extends Updater<ABDAMED2DataSource> {
    public ABDAMED2Updater(final ABDAMED2DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) throws UpdaterException {
        throw new UpdaterOnlyManuallyException();
    }
}
