package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;

public class HGNCMappingDescriber extends MappingDescriber {
    public HGNCMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (HGNCGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (HGNCGraphExporter.PROTEIN_LABEL.equals(localMappingLabel))
            return describeProtein(node);
        if (HGNCGraphExporter.MI_RNA_LABEL.equals(localMappingLabel))
            return describeMiRNA(node);
        return null;
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("alias_names"));
        description.addIdentifier(IdentifierType.HGNC_ID, getHGNCIdFromNode(node));
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, node.<String>getProperty("symbol"));
        final String[] omimIds = node.getProperty("omim_ids");
        if (omimIds != null)
            for (final String omimId : omimIds)
                description.addIdentifier(IdentifierType.OMIM, Integer.parseInt(omimId));
        description.addIdentifier(IdentifierType.ENSEMBL, node.<String>getProperty("ensembl_gene_id"));
        description.addIdentifier(IdentifierType.NCBI_GENE, node.<Integer>getProperty("entrez_id"));
        return new NodeMappingDescription[]{description};
    }

    private Integer getHGNCIdFromNode(final Node node) {
        final String id = node.getProperty(HGNCGraphExporter.HGNC_ID_KEY);
        return Integer.parseInt(id.replace("HGNC:", ""));
    }

    private NodeMappingDescription[] describeProtein(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
        description.addIdentifier(IdentifierType.UNIPROT_KB,
                                  node.<String>getProperty(HGNCGraphExporter.UNIPROT_ID_KEY));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMiRNA(final Node node) {
        final NodeMappingDescription description = new RNANodeMappingDescription(
                RNANodeMappingDescription.RNAType.MI_RNA);
        description.addIdentifier(IdentifierType.MIRBASE, node.<String>getProperty("mirbase_accession"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                HGNCGraphExporter.GENE_LABEL, HGNCGraphExporter.PROTEIN_LABEL, HGNCGraphExporter.MI_RNA_LABEL
        };
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        if (edges.length == 1 && edges[0].getLabel().endsWith(HGNCGraphExporter.CODES_FOR_LABEL))
            return new PathMappingDescription(PathMappingDescription.EdgeType.CODES_FOR);
        return null;
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[]{
                new PathMapping().add(HGNCGraphExporter.GENE_LABEL, HGNCGraphExporter.CODES_FOR_LABEL,
                                      HGNCGraphExporter.PROTEIN_LABEL, EdgeDirection.FORWARD)
        };
    }
}
