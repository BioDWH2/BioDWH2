package de.unibi.agbi.biodwh2.iig.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class IIGMappingDescriber extends MappingDescriber {
    public IIGMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (IIGGraphExporter.INGREDIENT_LABEL.equals(localMappingLabel))
            return describeIngredient(node);
        return null;
    }

    private NodeMappingDescription[] describeIngredient(final Node node) {
        final String unii = node.getProperty("unii");
        if (unii == null)
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addIdentifier(IdentifierType.UNII, unii);
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{IIGGraphExporter.INGREDIENT_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
