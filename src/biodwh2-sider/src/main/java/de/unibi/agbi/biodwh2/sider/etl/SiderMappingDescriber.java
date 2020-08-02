package de.unibi.agbi.biodwh2.sider.etl;

import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class SiderMappingDescriber extends MappingDescriber {
    @Override
    public NodeMappingDescription describe(Graph graph, Node node) {
        switch (node.getLabel()) {
            case "Drug": {
                NodeMappingDescription description = new NodeMappingDescription();
                description.type = NodeMappingDescription.NodeType.DRUG;
                String id = StringUtils.stripStart(node.getProperty("id"), "CID");
                description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, "" + Long.parseLong(id));
                return description;
            }
            case "Disease": {
                NodeMappingDescription description = new NodeMappingDescription();
                description.type = NodeMappingDescription.NodeType.DISEASE;
                description.addIdentifier(IdentifierType.UMLS_CUI, node.getProperty("id"));
                return description;
            }
            case "SideEffect": {
                NodeMappingDescription description = new NodeMappingDescription();
                description.type = NodeMappingDescription.NodeType.SIDE_EFFECT;
                description.addIdentifier(IdentifierType.UMLS_CUI, node.getProperty("id"));
                return description;
            }
        }
        return null;
    }

    @Override
    public EdgeMappingDescription describe(Graph graph, Edge edge) {
        return null;
    }
}
