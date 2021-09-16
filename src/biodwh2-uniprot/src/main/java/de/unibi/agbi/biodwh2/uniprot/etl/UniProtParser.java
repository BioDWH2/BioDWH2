package de.unibi.agbi.biodwh2.uniprot.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.uniprot.UniProtDataSource;

public class UniProtParser extends Parser<UniProtDataSource> {
    public UniProtParser(final UniProtDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) {
        return true;
    }
}
