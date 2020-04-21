package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;

public class DisGeNetGraphExporter extends GraphExporter<DisGeNetDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final DisGeNetDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        return true;
    }
}
