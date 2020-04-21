package de.unibi.agbi.biodwh2.sider.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.exceptions.UpdaterException;
import de.unibi.agbi.biodwh2.core.model.Version;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;

public class SiderUpdater extends Updater<SiderDataSource> {
    @Override
    public Version getNewestVersion() throws UpdaterException {
        return null;
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, SiderDataSource dataSource) throws UpdaterException {
        return false;
    }
}
