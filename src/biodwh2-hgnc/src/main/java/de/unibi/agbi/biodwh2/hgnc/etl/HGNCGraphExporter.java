package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

public class HGNCGraphExporter extends GraphExporter<HGNCDataSource> {
    public HGNCGraphExporter(final HGNCDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.setNodeIndexPropertyKeys("hgnc_id", "symbol");
        for (final Gene gene : dataSource.genes)
            createNodeFromModel(graph, gene);
        return true;
    }
}
