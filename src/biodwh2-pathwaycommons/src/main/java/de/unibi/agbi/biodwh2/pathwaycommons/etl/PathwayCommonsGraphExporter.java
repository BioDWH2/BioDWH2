package de.unibi.agbi.biodwh2.pathwaycommons.etl;

import de.unibi.agbi.biodwh2.core.etl.BioPaxGraphExporter;
import de.unibi.agbi.biodwh2.pathwaycommons.PathwayCommonsDataSource;

public class PathwayCommonsGraphExporter extends BioPaxGraphExporter<PathwayCommonsDataSource> {
    public PathwayCommonsGraphExporter(final PathwayCommonsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return super.getExportVersion() + 2;
    }

    @Override
    protected String getFileName() {
        return PathwayCommonsUpdater.ALL_BIOPAX_OWL_FILE_NAME;
    }
}
