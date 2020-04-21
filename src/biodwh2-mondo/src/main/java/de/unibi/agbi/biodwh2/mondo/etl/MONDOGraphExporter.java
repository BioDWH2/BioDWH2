package de.unibi.agbi.biodwh2.mondo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.mondo.MONDODataSource;

public class MONDOGraphExporter extends GraphExporter<MONDODataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final MONDODataSource dataSource,
                                  final Graph graph) throws ExporterException {
        return true;
    }
}
