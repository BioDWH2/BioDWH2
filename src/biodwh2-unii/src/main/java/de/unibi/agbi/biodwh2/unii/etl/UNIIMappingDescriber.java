package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class UNIIMappingDescriber extends MappingDescriber {
    public UNIIMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("UNII".equals(localMappingLabel)) {
            final NodeMappingDescription description = new NodeMappingDescription(
                    NodeMappingDescription.NodeType.COMPOUND);
            description.addName(node.getProperty("name"));
            description.addName(node.getProperty("preferred_term"));
            description.addNames(node.<String[]>getProperty("official_names"));
            description.addIdentifier(IdentifierType.UNII, node.getProperty("id"));
            description.addIdentifier(IdentifierType.CAS, node.getProperty("cas"));
            description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, node.getProperty("pubchem_cid"));
            description.addIdentifier(IdentifierType.EUROPEAN_CHEMICALS_AGENCY_EC, node.getProperty("ec"));
            description.addIdentifier(IdentifierType.RX_NORM_CUI, node.getProperty("rx_cui"));
            return description;
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"UNII"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
