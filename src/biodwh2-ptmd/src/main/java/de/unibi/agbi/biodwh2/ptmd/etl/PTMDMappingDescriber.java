package de.unibi.agbi.biodwh2.ptmd.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class PTMDMappingDescriber extends MappingDescriber {
    public PTMDMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (PTMDGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProteinNode(node);
        return null;
    }

    private NodeMappingDescription[] describeProteinNode(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB, node.<String>getProperty("uniprot_accession"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{PTMDGraphExporter.PROTEIN_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
