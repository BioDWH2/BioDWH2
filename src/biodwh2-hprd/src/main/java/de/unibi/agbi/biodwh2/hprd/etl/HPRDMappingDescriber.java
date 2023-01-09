package de.unibi.agbi.biodwh2.hprd.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;

public class HPRDMappingDescriber extends MappingDescriber {
    public HPRDMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (HPRDGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (HPRDGraphExporter.M_RNA_LABEL.equals(localMappingLabel))
            return describeMRNA(node);
        if (HPRDGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty("entrez_gene_id"));
        description.addIdentifier(IdentifierType.OMIM, node.<Integer>getProperty("omim_id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMRNA(final Node node) {
        final NodeMappingDescription description = new RNANodeMappingDescription(
                RNANodeMappingDescription.RNAType.M_RNA);
        final String refSeqId = node.getProperty(HPRDGraphExporter.REFSEQ_ID_KEY);
        if (refSeqId != null)
            description.addIdentifier(IdentifierType.GENBANK, removeGenbankIdVersion(refSeqId));
        return new NodeMappingDescription[]{description};
    }

    private String removeGenbankIdVersion(final String id) {
        final int indexOfVersionDot = id.lastIndexOf('.');
        return indexOfVersionDot == -1 ? id : id.substring(0, indexOfVersionDot);
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        final String refSeqId = node.getProperty(HPRDGraphExporter.REFSEQ_ID_KEY);
        if (refSeqId != null)
            description.addIdentifier(IdentifierType.GENBANK, removeGenbankIdVersion(refSeqId));
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                HPRDGraphExporter.GENE_LABEL, HPRDGraphExporter.M_RNA_LABEL, HPRDGraphExporter.PROTEIN_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
