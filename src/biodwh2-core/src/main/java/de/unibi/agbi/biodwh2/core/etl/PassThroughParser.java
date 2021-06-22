package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;

public final class PassThroughParser<D extends DataSource> extends Parser<D> {
    public PassThroughParser(final D dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        return true;
    }
}
