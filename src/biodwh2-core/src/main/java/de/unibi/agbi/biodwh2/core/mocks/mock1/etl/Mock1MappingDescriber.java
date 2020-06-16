package de.unibi.agbi.biodwh2.core.mocks.mock1.etl;

import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class Mock1MappingDescriber extends MappingDescriber {
    @Override
    public NodeMappingDescription describe(Graph graph, Node node) {
        NodeMappingDescription description = new NodeMappingDescription();
        if (node.getLabels()[0].endsWith("Gene")) {
            description.type = NodeMappingDescription.NodeType.Gene;
            description.addIdentifier(IdentifierType.HGNCSymbol, node.getProperty("hgnc_id"));
        } else if (node.getLabels()[0].endsWith("Dummy1")) {
            description.type = NodeMappingDescription.NodeType.Dummy;
            description.addIdentifier(IdentifierType.Dummy, node.getProperty("id"));
            if (node.hasProperty("id2"))
                description.addIdentifier(IdentifierType.Dummy, node.getProperty("id2"));
        }
        return description;
    }

    @Override
    public EdgeMappingDescription describe(Graph graph, Edge edge) {
        return null;
    }
}
