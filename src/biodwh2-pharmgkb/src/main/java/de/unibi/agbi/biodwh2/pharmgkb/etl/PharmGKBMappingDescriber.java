package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class PharmGKBMappingDescriber extends MappingDescriber {
    public PharmGKBMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (PharmGKBGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (PharmGKBGraphExporter.CHEMICAL_LABEL.equals(localMappingLabel))
            return describeChemical(node);
        if (PharmGKBGraphExporter.HAPLOTYPE_LABEL.equals(localMappingLabel) ||
            PharmGKBGraphExporter.HAPLOTYPE_SET_LABEL.equals(localMappingLabel))
            return describeHaplotype(node);
        if (PharmGKBGraphExporter.GENE_LABEL.equals(localMappingLabel))
            return describeGene(node);
        if (PharmGKBGraphExporter.VARIANT_LABEL.equals(localMappingLabel))
            return describeVariant(node);
        if (PharmGKBGraphExporter.PATHWAY_LABEL.equals(localMappingLabel))
            return describePathway(node);
        return null;
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.<String>getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (final String reference : crossReferences)
            if (reference.startsWith("DrugBank"))
                description.addIdentifier(IdentifierType.DRUG_BANK, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("KEGG Drug"))
                description.addIdentifier(IdentifierType.KEGG, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("Chemical Abstracts Service"))
                description.addIdentifier(IdentifierType.CAS, StringUtils.split(reference, ":", 2)[1]);
        return new NodeMappingDescription[]{description};
    }

    private String[] getCrossReferences(final Node node) {
        return node.hasProperty("cross_references") ? node.getProperty("cross_references") : new String[0];
    }

    private NodeMappingDescription[] describeChemical(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.<String>getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (final String reference : crossReferences)
            if (reference.startsWith("DrugBank"))
                description.addIdentifier(IdentifierType.DRUG_BANK, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("PubChem Compound"))
                description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, StringUtils.split(reference, ":", 2)[1]);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeHaplotype(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(
                NodeMappingDescription.NodeType.HAPLOTYPE);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.<String>getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.<String>getProperty("id"));
        final String geneSymbol = node.getProperty("symbol");
        if (geneSymbol != null)
            description.addIdentifier(IdentifierType.HGNC_SYMBOL, geneSymbol.trim());
        final String[] ensemblGeneIds = node.getProperty("ensemble_ids");
        if (ensemblGeneIds != null)
            for (final String ensemblGeneId : ensemblGeneIds)
                description.addIdentifier(IdentifierType.ENSEMBL_GENE_ID, ensemblGeneId.trim());
        final String[] crossReferences = getCrossReferences(node);
        for (final String reference : crossReferences)
            if (reference.startsWith("HGNC"))
                description.addIdentifier(IdentifierType.HGNC_ID, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("GeneCard"))
                description.addIdentifier(IdentifierType.GENE_CARD, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("GenAtlas"))
                description.addIdentifier(IdentifierType.GEN_ATLAS, StringUtils.split(reference, ":", 2)[1]);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeVariant(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.VARIANT);
        final String pharmGKBId = node.getProperty("id");
        final String name = node.getProperty("name");
        if (pharmGKBId != null)
            description.addIdentifier(IdentifierType.PHARM_GKB, pharmGKBId);
        if (name != null && name.startsWith("rs"))
            description.addIdentifier(IdentifierType.DB_SNP, name);
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describePathway(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.<String>getProperty("id"));
        return new NodeMappingDescription[]{description};
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                PharmGKBGraphExporter.DRUG_LABEL, PharmGKBGraphExporter.CHEMICAL_LABEL,
                PharmGKBGraphExporter.HAPLOTYPE_LABEL, PharmGKBGraphExporter.HAPLOTYPE_SET_LABEL,
                PharmGKBGraphExporter.GENE_LABEL, PharmGKBGraphExporter.VARIANT_LABEL,
                PharmGKBGraphExporter.PATHWAY_LABEL
        };
    }

    @Override
    public PathMappingDescription describe(Graph graph, Node[] nodes, Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }
}
