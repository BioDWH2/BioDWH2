package de.unibi.agbi.biodwh2.mirtarbase.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class MiRTarBaseMappingDescriber extends MappingDescriber {
    public MiRTarBaseMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (MiRTarBaseGraphExporter.MIRNA_LABEL.equals(localMappingLabel))
            return describeMiRNA(node);
        if (MiRTarBaseGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        return null;
    }

    private NodeMappingDescription[] describeMiRNA(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.RNA);
        final String id = node.getProperty("id");
        description.addIdentifier(IdentifierType.MIRNA, id);
        description.addName(id);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final String species = node.getProperty("species");
        if (!"Homo sapiens".equalsIgnoreCase(species))
            return null;
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty("entrez_gene_id"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                MiRTarBaseGraphExporter.MIRNA_LABEL, MiRTarBaseGraphExporter.GENE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
