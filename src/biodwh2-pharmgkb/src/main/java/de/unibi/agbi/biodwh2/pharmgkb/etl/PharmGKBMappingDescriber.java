package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import org.apache.commons.lang3.StringUtils;

public class PharmGKBMappingDescriber extends MappingDescriber {
    public PharmGKBMappingDescriber(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node) {
        if ("Drug".equalsIgnoreCase(node.getLabel()))
            return describeDrug(node);
        if ("Chemical".equalsIgnoreCase(node.getLabel()))
            return describeChemical(node);
        if ("Haplotype".equalsIgnoreCase(node.getLabel()) || "HaplotypeSet".equalsIgnoreCase(node.getLabel()))
            return describeHaplotype(node);
        if ("Gene".equalsIgnoreCase(node.getLabel()))
            return describeGene(node);
        if ("Variant".equalsIgnoreCase(node.getLabel()))
            return describeVariant(node);
        if ("Pathway".equalsIgnoreCase(node.getLabel()))
            return describePathway(node);
        return null;
    }

    private NodeMappingDescription describeDrug(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (String reference : crossReferences)
            if (reference.startsWith("DrugBank"))
                description.addIdentifier(IdentifierType.DRUG_BANK, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("KEGG Drug"))
                description.addIdentifier(IdentifierType.KEGG, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("Chemical Abstracts Service"))
                description.addIdentifier(IdentifierType.CAS, StringUtils.split(reference, ":", 2)[1]);
        return description;
    }

    private String[] getCrossReferences(final Node node) {
        return node.hasProperty("cross_references") ? node.getProperty("cross_references") : new String[0];
    }

    private NodeMappingDescription describeChemical(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (String reference : crossReferences)
            if (reference.startsWith("DrugBank"))
                description.addIdentifier(IdentifierType.DRUG_BANK, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("PubChem Compound"))
                description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, StringUtils.split(reference, ":", 2)[1]);
        return description;
    }

    private NodeMappingDescription describeHaplotype(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.HAPLOTYPE);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.getProperty("id"));
        return description;
    }

    private NodeMappingDescription describeGene(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (String reference : crossReferences)
            if (reference.startsWith("HGNC"))
                description.addIdentifier(IdentifierType.HGNC_ID, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("GeneCard"))
                description.addIdentifier(IdentifierType.GENE_CARD, StringUtils.split(reference, ":", 2)[1]);
            else if (reference.startsWith("GenAtlas"))
                description.addIdentifier(IdentifierType.GEN_ATLAS, StringUtils.split(reference, ":", 2)[1]);
        return description;
    }

    private NodeMappingDescription describeVariant(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.VARIANT);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.getProperty("id"));
        return description;
    }

    private NodeMappingDescription describePathway(final Node node) {
        NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.PATHWAY);
        description.addIdentifier(IdentifierType.PHARM_GKB, node.getProperty("id"));
        return description;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{"Drug", "Chemical", "Haplotype", "Gene", "Variant", "Pathway"};
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
