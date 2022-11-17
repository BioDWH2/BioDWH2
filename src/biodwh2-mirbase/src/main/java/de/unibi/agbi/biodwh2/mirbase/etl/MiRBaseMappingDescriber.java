package de.unibi.agbi.biodwh2.mirbase.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class MiRBaseMappingDescriber extends MappingDescriber {
    public MiRBaseMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (MiRBaseGraphExporter.PRE_MI_RNA_LABEL.equals(localMappingLabel))
            return describePreMiRNA(node);
        if (MiRBaseGraphExporter.MI_RNA_LABEL.equals(localMappingLabel))
            return describeMiRNA(node);
        if (MiRBaseGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        return null;
    }

    private NodeMappingDescription[] describePreMiRNA(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.RNA);
        description.addIdentifier(IdentifierType.MIRBASE, node.<String>getProperty("accession"));
        description.addName(node.getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMiRNA(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.RNA);
        description.addIdentifier(IdentifierType.MIRBASE, node.<String>getProperty("accession"));
        description.addName(node.getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        final Integer hgncId = node.<Integer>getProperty("hgnc_id");
        if (hgncId != null)
            description.addIdentifier(IdentifierType.HGNC_ID, hgncId);
        final Integer entrezId = node.<Integer>getProperty("entrez_gene_id");
        if (entrezId != null)
            description.addIdentifier(IdentifierType.ENTREZ_GENE_ID, entrezId);
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                MiRBaseGraphExporter.PRE_MI_RNA_LABEL, MiRBaseGraphExporter.MI_RNA_LABEL,
                MiRBaseGraphExporter.GENE_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
