package de.unibi.agbi.biodwh2.core.mocks.mock2.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.mocks.mock2.Mock2DataSource;

public class Mock2Parser extends Parser<Mock2DataSource> {
    @Override
    public boolean parse(Workspace workspace, Mock2DataSource dataSource) throws ParserException {
        return true;
    }
}
