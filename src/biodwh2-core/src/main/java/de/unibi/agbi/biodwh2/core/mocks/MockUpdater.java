package de.unibi.agbi.biodwh2.core.mocks;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.core.model.Version;

public class MockUpdater<T extends DataSource> extends Updater<T> {
    public MockUpdater(final T dataSource) {
        super(dataSource);
    }

    @Override
    public Version getNewestVersion(final Workspace workspace) {
        return new Version(1, 0);
    }

    @Override
    protected boolean tryUpdateFiles(final Workspace workspace) {
        return true;
    }
}
