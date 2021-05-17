package de.unibi.agbi.biodwh2.core.mocks.mock3.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class Mock3MappingDescriber extends MappingDescriber {
    public Mock3MappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Test".equals(localMappingLabel)) {
            final NodeMappingDescription d = new NodeMappingDescription(NodeMappingDescription.NodeType.DUMMY);
            d.addIdentifier(IdentifierType.DUMMY, "MOCK3:" + node.getProperty("id"));
            return new NodeMappingDescription[]{d};
        }
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Test"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges[0].getLabel().endsWith("HAS_PREVIOUS"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.DUMMY);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add("Test", "HAS_PREVIOUS", "Test", PathMapping.EdgeDirection.FORWARD)
        };
    }
}
