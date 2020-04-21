package de.unibi.agbi.biodwh2.sider.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.sider.SiderDataSource;

public class SiderGraphExporter extends GraphExporter<SiderDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final SiderDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        return true;
    }
}
