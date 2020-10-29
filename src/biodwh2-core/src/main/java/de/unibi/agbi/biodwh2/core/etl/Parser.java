package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;

public abstract class Parser<D extends DataSource> {
    protected final D dataSource;

    public Parser(final D dataSource) {
        this.dataSource = dataSource;
    }

    public abstract boolean parse(final Workspace workspace) throws ParserException;
}
