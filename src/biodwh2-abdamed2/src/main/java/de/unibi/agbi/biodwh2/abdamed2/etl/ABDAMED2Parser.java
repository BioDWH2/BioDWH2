package de.unibi.agbi.biodwh2.abdamed2.etl;

import de.unibi.agbi.biodwh2.abdamed2.ABDAMED2DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;

public class ABDAMED2Parser extends Parser<ABDAMED2DataSource> {
    public ABDAMED2Parser(final ABDAMED2DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        return true;
    }
}
