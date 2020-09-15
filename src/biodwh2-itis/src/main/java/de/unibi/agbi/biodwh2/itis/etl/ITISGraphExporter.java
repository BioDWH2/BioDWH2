package de.unibi.agbi.biodwh2.itis.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.itis.ITISDataSource;

public class ITISGraphExporter extends GraphExporter<ITISDataSource> {
    public ITISGraphExporter(ITISDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        return false;
    }
}
