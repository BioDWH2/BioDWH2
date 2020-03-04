package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;

public class DGIdbParser extends Parser<DGIdbDataSource> {
    @Override
    public boolean parse(Workspace workspace, DGIdbDataSource dataSource) throws ParserException {
        return false;
    }
}
