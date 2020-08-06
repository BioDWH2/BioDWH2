package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class UNIIMappingDescriber extends MappingDescriber {
    public UNIIMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription describe(Graph graph, Node node) {
        if (node.getLabel().endsWith("UNII")) {
            NodeMappingDescription description = new NodeMappingDescription();
            description.type = NodeMappingDescription.NodeType.COMPOUND;
            description.addIdentifier(IdentifierType.UNII, node.getProperty("id"));
            if (node.hasProperty("cas") && node.getProperty("cas") != null)
                description.addIdentifier(IdentifierType.CAS, node.getProperty("cas"));
            if (node.hasProperty("pubchem_cid") && node.getProperty("pubchem_cid") != null)
                description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, node.getProperty("pubchem_cid"));
            if (node.hasProperty("ec") && node.getProperty("ec") != null)
                description.addIdentifier(IdentifierType.EUROPEAN_CHEMICALS_AGENCY_EC, node.getProperty("ec"));
            if (node.hasProperty("rx_cui") && node.getProperty("rx_cui") != null)
                description.addIdentifier(IdentifierType.RX_NORM_CUI, node.getProperty("rx_cui"));
            // TODO: more ids
            return description;
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"UNII"};
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
