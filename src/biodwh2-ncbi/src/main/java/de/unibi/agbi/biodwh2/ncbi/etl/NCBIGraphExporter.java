package de.unibi.agbi.biodwh2.ncbi.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.ncbi.NCBIDataSource;

public class NCBIGraphExporter extends GraphExporter<NCBIDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final NCBIDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        return true;
    }
}
