package de.unibi.agbi.biodwh2.redodb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.mapping.IdentifierUtils;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public final class ReDODBMappingDescriber extends MappingDescriber {
    public ReDODBMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Drug".equals(localMappingLabel))
            return describeDrug(node);
        if ("Gene".equals(localMappingLabel))
            return describeGene(node);
        if ("Disease".equals(localMappingLabel))
            return describeDisease(node);
        return new NodeMappingDescription[0];
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.DRUG_BANK, node.<String>getProperty("drugbank_id"));
        final String pubChemId = node.<String>getProperty("pubchem_id");
        if (IdentifierUtils.isCasNumber(pubChemId))
            description.addIdentifier(IdentifierType.CAS, pubChemId);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("symbol"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        // TODO: no id provided
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges[0].getLabel().endsWith("INDICATES"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INDICATES);
        if (edges[0].getLabel().endsWith("TARGETS"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.TARGETS);
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug", "Gene", "Disease"};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add("Drug", "INDICATES", "Disease", PathMapping.EdgeDirection.FORWARD),
                new PathMapping().add("Drug", "TARGETS", "Gene", PathMapping.EdgeDirection.FORWARD)
        };
    }
}
