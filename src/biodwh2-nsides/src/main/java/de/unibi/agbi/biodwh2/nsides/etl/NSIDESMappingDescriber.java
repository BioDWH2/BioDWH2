package de.unibi.agbi.biodwh2.nsides.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class NSIDESMappingDescriber extends MappingDescriber {
    public NSIDESMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (NSIDESGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (NSIDESGraphExporter.DRUG_EFFECT_LABEL.equals(localMappingLabel))
            return describeDrugEffect(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.RX_NORM_CUI, node.<String>getProperty(GraphExporter.ID_KEY));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDrugEffect(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.ADVERSE_EVENT);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.MEDDRA, node.<String>getProperty(GraphExporter.ID_KEY));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{NSIDESGraphExporter.DRUG_LABEL, NSIDESGraphExporter.DRUG_EFFECT_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
