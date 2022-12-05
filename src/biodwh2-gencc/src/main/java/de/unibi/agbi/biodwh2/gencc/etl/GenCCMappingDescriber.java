package de.unibi.agbi.biodwh2.gencc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class GenCCMappingDescriber extends MappingDescriber {
    public GenCCMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (GenCCGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (GenCCGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        final String id = node.getProperty("id");
        if (id != null && id.startsWith("HGNC"))
            description.addIdentifier(IdentifierType.HGNC_ID, Integer.parseInt(id.replace("HGNC:", "")));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        final String id = node.getProperty("id");
        if (id != null && id.startsWith("MONDO"))
            description.addIdentifier(IdentifierType.MONDO, id.replace("MONDO:", ""));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{GenCCGraphExporter.GENE_LABEL, GenCCGraphExporter.DISEASE_LABEL};
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
