package de.unibi.agbi.biodwh2.pathwaycommons.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.MultiFileFTPWebUpdater;
import de.unibi.agbi.biodwh2.pathwaycommons.PathwayCommonsDataSource;

public class PathwayCommonsUpdater extends MultiFileFTPWebUpdater<PathwayCommonsDataSource> {
    static final String PATHWAYS_FILE_NAME = "pathways.txt.gz";
    static final String DATA_SOURCES_FILE_NAME = "datasources.txt";
    static final String ALL_UNIPROT_GMT_FILE_PATH = "PathwayCommons12.All.uniprot.gmt.gz";
    static final String ALL_HGNC_TXT_FILE_PATH = "PathwayCommons12.All.hgnc.txt.gz";
    static final String ALL_HGNC_SIF_FILE_PATH = "PathwayCommons12.All.hgnc.sif.gz";
    static final String ALL_HGNC_GMT_FILE_PATH = "PathwayCommons12.All.hgnc.gmt.gz";
    static final String ALL_BIOPAX_OWL_FILE_PATH = "PathwayCommons12.All.BIOPAX.owl.gz";

    public PathwayCommonsUpdater(final PathwayCommonsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getFTPIndexUrl() {
        return "https://www.pathwaycommons.org/archives/PC2/v12/";
    }

    @Override
    protected String[] getFilePaths(final Workspace workspace) {
        return new String[]{
                PATHWAYS_FILE_NAME, DATA_SOURCES_FILE_NAME, ALL_UNIPROT_GMT_FILE_PATH, ALL_HGNC_TXT_FILE_PATH,
                ALL_HGNC_SIF_FILE_PATH, ALL_HGNC_GMT_FILE_PATH, ALL_BIOPAX_OWL_FILE_PATH
        };
    }

    @Override
    protected String[] expectedFileNames() {
        return new String[]{
                PATHWAYS_FILE_NAME, DATA_SOURCES_FILE_NAME, ALL_UNIPROT_GMT_FILE_PATH, ALL_HGNC_TXT_FILE_PATH,
                ALL_HGNC_SIF_FILE_PATH, ALL_HGNC_GMT_FILE_PATH, ALL_BIOPAX_OWL_FILE_PATH
        };
    }
}
