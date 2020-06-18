package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class HGNCMappingDescriber extends MappingDescriber {
    @Override
    public NodeMappingDescription describe(Graph graph, Node node) {
        if (node.getLabels()[0].endsWith("Gene")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.Gene;
            description.addIdentifier(IdentifierType.HGNCId, node.<String>getProperty("hgnc_id").replace("HGNC:", ""));
            description.addIdentifier(IdentifierType.HGNCSymbol, node.getProperty("symbol"));
            // TODO: more ids
            return description;
        }
        return null;
    }

    @Override
    public EdgeMappingDescription describe(Graph graph, Edge edge) {
        return null;
    }
}
