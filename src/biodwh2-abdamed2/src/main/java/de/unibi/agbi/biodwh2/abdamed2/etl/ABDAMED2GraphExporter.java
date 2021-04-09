package de.unibi.agbi.biodwh2.abdamed2.etl;

import de.unibi.agbi.biodwh2.abdamed2.ABDAMED2DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;

public class ABDAMED2GraphExporter extends GraphExporter<ABDAMED2DataSource> {
    public ABDAMED2GraphExporter(final ABDAMED2DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        return false;
    }
}
