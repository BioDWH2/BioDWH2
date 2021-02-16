package de.unibi.agbi.biodwh2.usdaplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.usdaplants.USDAPlantsDataSource;
import de.unibi.agbi.biodwh2.usdaplants.model.Plant;

public class USDAPlantsGraphExporter extends GraphExporter<USDAPlantsDataSource> {
    public USDAPlantsGraphExporter(final USDAPlantsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.setNodeIndexPropertyKeys("symbol");
        for (final Plant plant : dataSource.plants)
            createNodeFromModel(graph, plant);
        return true;
    }
}
