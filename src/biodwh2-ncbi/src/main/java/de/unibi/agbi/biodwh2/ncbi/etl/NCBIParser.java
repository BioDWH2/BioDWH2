package de.unibi.agbi.biodwh2.ncbi.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.ncbi.NCBIDataSource;

public class NCBIParser extends Parser<NCBIDataSource> {
    @Override
    public boolean parse(Workspace workspace, NCBIDataSource dataSource) throws ParserException {
        return false;
    }
}
