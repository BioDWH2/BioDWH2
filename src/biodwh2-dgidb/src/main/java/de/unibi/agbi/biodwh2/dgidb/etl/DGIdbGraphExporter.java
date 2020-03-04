package de.unibi.agbi.biodwh2.dgidb.etl;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.dgidb.DGIdbDataSource;

public class DGIdbGraphExporter extends GraphExporter<DGIdbDataSource> {
    @Override
    protected Graph exportGraph(DGIdbDataSource dataSource) {
        return new Graph();
    }
}
