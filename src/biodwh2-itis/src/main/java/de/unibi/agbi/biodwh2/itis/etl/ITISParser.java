package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;

public class ITISParser extends Parser<ITISDataSource> {
    public ITISParser(ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(Workspace workspace) throws ParserException {
        return false;
    }
}
