package de.unibi.agbi.biodwh2.core.mocks.mock2.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public final class Mock2MappingDescriber extends MappingDescriber {
    public Mock2MappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Gene".equals(localMappingLabel)) {
            NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
            description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("id").replace("HGNC:", ""));
            return new NodeMappingDescription[]{description};
        }
        if ("Dummy2".equals(localMappingLabel)) {
            NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DUMMY);
            description.addIdentifier(IdentifierType.DUMMY, node.<String>getProperty("id"));
            if (node.hasProperty("id2"))
                description.addIdentifier(IdentifierType.DUMMY, node.<String>getProperty("id2"));
            return new NodeMappingDescription[]{description};
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Gene", "Dummy2"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
