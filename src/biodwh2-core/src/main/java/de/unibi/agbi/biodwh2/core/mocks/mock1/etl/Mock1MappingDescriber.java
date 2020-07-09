package de.unibi.agbi.biodwh2.core.mocks.mock1.etl;

import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class Mock1MappingDescriber extends MappingDescriber {
    @Override
    public NodeMappingDescription describe(Graph graph, Node node) {
        if (node.getLabel().endsWith("Gene")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.Gene;
            description.addIdentifier(IdentifierType.HGNCSymbol, node.getProperty("hgnc_id"));
            return description;
        } else if (node.getLabel().endsWith("Drug")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.Drug;
            description.addIdentifier(IdentifierType.DrugBank, node.getProperty("drugbank_id"));
            return description;
        } else if (node.getLabel().endsWith("Dummy1")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.Dummy;
            description.addIdentifier(IdentifierType.Dummy, node.getProperty("id"));
            if (node.hasProperty("id2"))
                description.addIdentifier(IdentifierType.Dummy, node.getProperty("id2"));
            return description;
        }
        return null;
    }

    @Override
    public EdgeMappingDescription describe(Graph graph, Edge edge) {
        if (edge.getLabel().endsWith("TARGETS")) {
            EdgeMappingDescription description = new EdgeMappingDescription();
            description.type = EdgeMappingDescription.EdgeType.Targets;
            return description;
        }
        return null;
    }
}
