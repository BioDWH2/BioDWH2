package de.unibi.agbi.biodwh2.iid.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class IIDMappingDescriber extends MappingDescriber {
    public IIDMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (IIDGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        return null;
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty(GraphExporter.ID_KEY));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1)
            return new PathMappingDescription(PathMappingDescription.EdgeType.INTERACTS);
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{IIDGraphExporter.PROTEIN_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        final var ppiPath = new PathMapping().add(IIDGraphExporter.PROTEIN_LABEL, IIDGraphExporter.INTERACTS_WITH_LABEL,
                                                  IIDGraphExporter.PROTEIN_LABEL);
        return new PathMapping[]{ppiPath};
    }
}
