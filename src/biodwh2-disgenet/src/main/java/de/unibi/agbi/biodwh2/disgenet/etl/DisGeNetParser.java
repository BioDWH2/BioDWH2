package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;

public class DisGeNetParser extends Parser<DisGeNetDataSource> {
    @Override
    public boolean parse(Workspace workspace, DisGeNetDataSource dataSource) throws ParserException {
        return false;
    }
}
