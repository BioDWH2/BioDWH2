package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

public class HPOParser extends Parser<HPODataSource> {
    @Override
    public boolean parse(Workspace workspace, HPODataSource dataSource) throws ParserException {
        return false;
    }
}
