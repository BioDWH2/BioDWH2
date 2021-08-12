package de.unibi.agbi.biodwh2.pathwaycommons.etl;

import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.pathwaycommons.PathwayCommonsDataSource;

public class PathwayCommonsUpdater extends MultiFileFTPWebUpdater<PathwayCommonsDataSource> {
    public PathwayCommonsUpdater(final PathwayCommonsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://www.pathwaycommons.org/archives/PC2/v12/";
    }

    @Override
    protected String[] getFilePaths() {
        return new String[]{
                "pathways.txt.gz", "datasources.txt", "PathwayCommons12.All.uniprot.gmt.gz",
                "PathwayCommons12.All.hgnc.txt.gz", "PathwayCommons12.All.hgnc.sif.gz",
                "PathwayCommons12.All.hgnc.gmt.gz", "PathwayCommons12.All.BIOPAX.owl.gz"
        };
    }

    @Override
    protected String[] expectedFileNames() {
        return getFilePaths();
    }
}
