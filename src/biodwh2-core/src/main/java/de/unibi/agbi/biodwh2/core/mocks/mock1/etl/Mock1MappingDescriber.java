package de.unibi.agbi.biodwh2.core.mocks.mock1.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public final class Mock1MappingDescriber extends MappingDescriber {
    public Mock1MappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node) {
        if (node.getLabel().endsWith("Gene")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.GENE;
            description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.getProperty("hgnc_id"));
            return description;
        } else if (node.getLabel().endsWith("Drug")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.DRUG;
            description.addIdentifier(IdentifierType.DRUG_BANK, node.getProperty("drugbank_id"));
            return description;
        } else if (node.getLabel().endsWith("Dummy1")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.DUMMY;
            description.addIdentifier(IdentifierType.DUMMY, node.getProperty("id"));
            if (node.hasProperty("id2"))
                description.addIdentifier(IdentifierType.DUMMY, node.getProperty("id2"));
            return description;
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Gene", "Drug", "Dummy1"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges[0].getLabel().endsWith("TARGETS")) {
            PathMappingDescription description = new PathMappingDescription();
            description.type = PathMappingDescription.EdgeType.TARGETS;
            return description;
        }
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[][]{
                {"Drug", "TARGETS", "Gene"}
        };
    }
}
