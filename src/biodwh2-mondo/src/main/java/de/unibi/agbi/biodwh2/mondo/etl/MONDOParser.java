package de.unibi.agbi.biodwh2.mondo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.mondo.MONDODataSource;

public class MONDOParser extends Parser<MONDODataSource> {
    @Override
    public boolean parse(Workspace workspace, MONDODataSource dataSource) throws ParserException {
        return false;
    }
}
