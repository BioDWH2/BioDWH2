package de.unibi.agbi.biodwh2.usdaplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.usdaplants.USDAPlantsDataSource;
import de.unibi.agbi.biodwh2.usdaplants.model.Plant;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class USDAPlantsGraphExporter extends GraphExporter<USDAPlantsDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(USDAPlantsGraphExporter.class);

    public USDAPlantsGraphExporter(final USDAPlantsDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Plant", "symbol", IndexDescription.Type.UNIQUE));
        // First, add all non-synonym plants
        dataSource.plants.stream().filter(p -> StringUtils.isEmpty(p.synonymSymbol)).forEach(p -> addPlant(graph, p));
        // Second, add all synonym plants and link them to the main plant node
        for (final Plant plant : dataSource.plants) {
            if (StringUtils.isNotEmpty(plant.synonymSymbol)) {
                final Node node = addSynonymPlant(graph, plant);
                final Node parent = graph.findNode("Plant", "symbol", plant.symbol);
                if (parent != null)
                    graph.addEdge(parent, node, "HAS_SYNONYM");
                else
                    LOGGER.warn("Failed to link synonym plant '" + plant.synonymSymbol + "' to plant '" + plant.symbol +
                                "'");
            }
        }
        return true;
    }

    private void addPlant(final Graph graph, final Plant plant) {
        final NodeBuilder builder = graph.buildNode().withLabel("Plant");
        builder.withProperty("symbol", plant.symbol);
        builder.withPropertyIfNotNull("common_name", plant.commonName);
        builder.withPropertyIfNotNull("family", plant.family);
        builder.withPropertyIfNotNull("scientific_name_with_author", plant.family);
        builder.build();
    }

    private Node addSynonymPlant(final Graph graph, final Plant plant) {
        // In rare cases, the synonym plant is already present as a main plant node
        final Node node = graph.findNode("Plant", "symbol", plant.synonymSymbol);
        if (node != null)
            return node;
        final NodeBuilder builder = graph.buildNode().withLabel("Plant");
        builder.withProperty("symbol", plant.synonymSymbol);
        builder.withPropertyIfNotNull("common_name", plant.commonName);
        builder.withPropertyIfNotNull("family", plant.family);
        builder.withPropertyIfNotNull("scientific_name_with_author", plant.family);
        return builder.build();
    }
}
