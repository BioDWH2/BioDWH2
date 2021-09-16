package de.unibi.agbi.biodwh2.usdaplants.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class USDAPlantsMappingDescriber extends MappingDescriber {
    public USDAPlantsMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Plant".equals(localMappingLabel))
            return describePlant(graph, node);
        return null;
    }

    private NodeMappingDescription[] describePlant(final Graph graph, final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.TAXON);
        description.addIdentifier(IdentifierType.USDA_PLANTS_SYMBOL, node.<String>getProperty("symbol"));
        final Long[] synonymNodeIds = graph.getAdjacentNodeIdsForEdgeLabel(node.getId(), "USDA-PLANTS_HAS_SYNONYM");
        for (final Long synonymNodeId : synonymNodeIds)
            description.addIdentifier(IdentifierType.USDA_PLANTS_SYMBOL,
                                      graph.getNode(synonymNodeId).<String>getProperty("symbol"));
        description.addName(node.getProperty("scientific_name_with_author"));
        description.addName(node.getProperty("common_name"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Plant"};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
