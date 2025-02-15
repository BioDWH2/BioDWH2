package de.unibi.agbi.biodwh2.pathwaycommons.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.pathwaycommons.PathwayCommonsDataSource;

public class PathwayCommonsUpdater extends MultiFileFTPWebUpdater<PathwayCommonsDataSource> {
    static final String ALL_BIOPAX_OWL_FILE_NAME = "pc-biopax.owl.gz";

    public PathwayCommonsUpdater(final PathwayCommonsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://download.baderlab.org/PathwayCommons/PC2/v14/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        return new String[]{ALL_BIOPAX_OWL_FILE_NAME};
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{ALL_BIOPAX_OWL_FILE_NAME};
    }
}
