package de.unibi.agbi.biodwh2.core.mocks.mock1.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.mocks.mock1.Mock1DataSource;
import de.unibi.agbi.biodwh2.core.model.Version;

public class Mock1Updater extends Updater<Mock1DataSource> {
    @Override
    public Version getNewestVersion() {
        return new Version(1, 0);
    }

    @Override
    protected boolean tryUpdateFiles(Workspace workspace, Mock1DataSource dataSource) {
        return true;
    }
}
