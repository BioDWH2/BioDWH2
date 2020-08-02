package de.unibi.agbi.biodwh2.core.mocks.mock1.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.mocks.mock1.Mock1DataSource;

public class Mock1Parser extends Parser<Mock1DataSource> {
    @Override
    public boolean parse(Workspace workspace, Mock1DataSource dataSource) {
        return true;
    }
}
