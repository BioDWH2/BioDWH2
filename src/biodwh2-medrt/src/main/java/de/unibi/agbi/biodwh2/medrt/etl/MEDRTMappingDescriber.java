package de.unibi.agbi.biodwh2.medrt.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class MEDRTMappingDescriber extends MappingDescriber {

    public MEDRTMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if ("Drug".equals(localMappingLabel))
            return describeDrug(node);
        if ("Disease".equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        final String namespace = node.getProperty("namespace");
        final String code = node.getProperty("code");
        if ("RxNorm".equals(namespace))
            description.addIdentifier(IdentifierType.RX_NORM_CUI, code);
        else if ("MeSH".equals(namespace))
            description.addIdentifier(IdentifierType.MESH, code);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("name"));
        final String namespace = node.getProperty("namespace");
        final String code = node.getProperty("code");
        if ("MeSH".equals(namespace))
            description.addIdentifier(IdentifierType.MESH, code);
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug", "Disease"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length > 0 && edges[0].getLabel().endsWith("INDUCES"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INDUCES);
        if (edges.length > 0 && edges[0].getLabel().endsWith("CI_WITH"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.CONTRAINDICATES);
        if (edges.length > 0 && edges[0].getLabel().endsWith("MAY_TREAT"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INDICATES);
        if (edges.length > 0 && edges[0].getLabel().endsWith("EFFECT_MAY_BE_INHIBITED_BY"))
            return new PathMappingDescription(PathMappingDescription.EdgeType.INTERACTS);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add("Drug", "INDUCES", "Disease", PathMapping.EdgeDirection.FORWARD),
                new PathMapping().add("Drug", "CI_WITH", "Disease", PathMapping.EdgeDirection.FORWARD),
                new PathMapping().add("Drug", "MAY_TREAT", "Disease", PathMapping.EdgeDirection.FORWARD),
                new PathMapping().add("Drug", "EFFECT_MAY_BE_INHIBITED_BY", "Drug", PathMapping.EdgeDirection.FORWARD)
        };
    }
}
