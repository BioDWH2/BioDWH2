package de.unibi.agbi.biodwh2.core.mocks.mock1.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public final class Mock1MappingDescriber extends MappingDescriber {
    public Mock1MappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Gene".equals(localMappingLabel))
            return describeGene(node);
        if ("Drug".equals(localMappingLabel))
            return describeDrug(node);
        if ("Dummy1".equals(localMappingLabel))
            return describeDummy(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("hgnc_id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription drugDescription = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        drugDescription.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        final NodeMappingDescription compoundDescription = new NodeMappingDescription(
                NodeMappingDescription.NodeType.COMPOUND);
        compoundDescription.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        return new NodeMappingDescription[]{drugDescription, compoundDescription};
    }

    private NodeMappingDescription[] describeDummy(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DUMMY);
        description.addIdentifier(IdentifierType.DUMMY, node.<String>getProperty("id"));
        if (node.hasProperty("id2"))
            description.addIdentifier(IdentifierType.DUMMY, node.<String>getProperty("id2"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Gene", "Drug", "Dummy1"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges[0].getLabel().endsWith("TARGETS"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add("Drug", "TARGETS", "Gene", EdgeDirection.FORWARD)
        };
    }
}
