package de.unibi.agbi.biodwh2.core.mocks.mock2.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.mocks.mock2.Mock2DataSource;

public final class Mock2Parser extends Parser<Mock2DataSource> {
    public Mock2Parser(final Mock2DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) {
        return true;
    }
}
