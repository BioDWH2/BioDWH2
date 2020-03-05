package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

public class HGNCGraphExporter extends GraphExporter<HGNCDataSource> {
    @Override
    protected Graph exportGraph(HGNCDataSource dataSource) throws ExporterException {
        Graph g = new Graph();
        for (Gene gene : dataSource.genes)
            createNodeFromModel(g, gene);
        return g;
    }
}
