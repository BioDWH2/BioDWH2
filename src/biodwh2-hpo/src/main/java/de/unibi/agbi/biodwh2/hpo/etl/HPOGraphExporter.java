package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

public class HPOGraphExporter extends GraphExporter<HPODataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final HPODataSource dataSource,
                                  final Graph graph) throws ExporterException {
        return true;
    }
}
