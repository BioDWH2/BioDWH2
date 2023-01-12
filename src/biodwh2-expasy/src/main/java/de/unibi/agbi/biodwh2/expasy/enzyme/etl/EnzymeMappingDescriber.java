package de.unibi.agbi.biodwh2.expasy.enzyme.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class EnzymeMappingDescriber extends MappingDescriber {
    public EnzymeMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (EnzymeGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        return null;
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.UNIPROT_KB,
                                  node.<String>getProperty(EnzymeGraphExporter.ACCESSION_KEY));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{EnzymeGraphExporter.PROTEIN_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
