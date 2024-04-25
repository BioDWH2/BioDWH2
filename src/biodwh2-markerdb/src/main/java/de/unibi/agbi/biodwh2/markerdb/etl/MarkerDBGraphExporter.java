package de.unibi.agbi.biodwh2.markerdb.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.markerdb.MarkerDBDataSource;

public class MarkerDBGraphExporter extends GraphExporter<MarkerDBDataSource> {
    public MarkerDBGraphExporter(final MarkerDBDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        return false;
    }
}
