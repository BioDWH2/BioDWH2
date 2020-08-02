package de.unibi.agbi.biodwh2.core.mocks.mock2.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.mocks.mock2.Mock2DataSource;
import de.unibi.agbi.biodwh2.core.model.Version;

public class Mock2Updater extends Updater<Mock2DataSource> {
    @Override
    public Version getNewestVersion() {
        return new Version(1, 0);
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, Mock2DataSource dataSource) {
        return true;
    }
}
