package de.unibi.agbi.biodwh2.pathwaycommons.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.pathwaycommons.PathwayCommonsDataSource;

public class PathwayCommonsParser extends Parser<PathwayCommonsDataSource> {
    public PathwayCommonsParser(final PathwayCommonsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) {
        return true;
    }
}
