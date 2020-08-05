package de.unibi.agbi.biodwh2.core.mocks.mock2.etl;

import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public final class Mock2MappingDescriber extends MappingDescriber {
    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node) {
        if (node.getLabel().endsWith("Gene")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.GENE;
            description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("id").replace("HGNC:", ""));
            return description;
        } else if (node.getLabel().endsWith("Dummy2")) {
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
    public EdgeMappingDescription describe(final Graph graph, final Edge edge) {
        return null;
    }
}
