package de.unibi.agbi.biodwh2.mondo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.mondo.MondoDataSource;

public class MondoGraphExporter extends GraphExporter<MondoDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final MondoDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        return true;
    }
}
