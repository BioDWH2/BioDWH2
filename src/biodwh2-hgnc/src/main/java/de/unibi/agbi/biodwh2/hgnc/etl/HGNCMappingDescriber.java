package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class HGNCMappingDescriber extends MappingDescriber {
    @Override
    public NodeMappingDescription describe(Graph graph, Node node) {
        NodeMappingDescription description = new NodeMappingDescription();
        if (node.getLabels()[0].endsWith("Gene")) {
            description.type = NodeMappingDescription.NodeType.Gene;
            description.addIdentifier(IdentifierType.HGNCId, node.<String>getProperty("hgnc_id").replace("HGNC:", ""));
            description.addIdentifier(IdentifierType.HGNCSymbol, node.getProperty("symbol"));
            // TODO: more ids
        }
        return description;
    }

    @Override
    public EdgeMappingDescription describe(Graph graph, Edge edge) {
        return null;
    }
}
