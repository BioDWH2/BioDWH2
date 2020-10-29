package de.unibi.agbi.biodwh2.ncbi.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.ncbi.NCBIDataSource;

public class NCBIParser extends Parser<NCBIDataSource> {
    public NCBIParser(final NCBIDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) {
        return false;
    }
}
