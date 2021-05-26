package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

public class HPOParser extends Parser<HPODataSource> {
    public HPOParser(final HPODataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) {
        return true;
    }
}
