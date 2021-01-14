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

    private String[] getCrossReferences(final Node node) {
        return node.hasProperty("cross_references") ? node.getProperty("cross_references") : new String[0];
    }

    private String getIdFromPrefixIdPair(final String pair) {
        return StringUtils.split(pair, ":", 2)[1];
    }

    private String[] getRxNormIdentifiers(final Node node) {
        return node.hasProperty("rxnorm_identifiers") ? node.getProperty("rxnorm_identifiers") : new String[0];
    }

    private NodeMappingDescription[] describeChemical(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.PHARM_GKB, node.<String>getProperty("id"));
        final String[] crossReferences = getCrossReferences(node);
        for (final String reference : crossReferences)
            if (reference.startsWith("DrugBank"))
                description.addIdentifier(IdentifierType.DRUG_BANK, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("PubChem Compound"))
                description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("Chemical Abstracts Service"))
                description.addIdentifier(IdentifierType.CAS, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("KEGG Compound"))
                description.addIdentifier(IdentifierType.KEGG, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("KEGG Drug"))
                description.addIdentifier(IdentifierType.KEGG, getIdFromPrefixIdPair(reference));
            /*
            "ChEBI", "Drugs Product Database (DPD)", "FDA Drug Label at DailyMed", "National Drug Code Directory",
            "PubChem Substance", "Therapeutic Targets Database", "URL", "ChemSpider", "BindingDB", "HET", "PDB",
            "IUPHAR Ligand", "HMDB", "ClinicalTrials.gov", "DrugBank Metabolite", "GenBank", "UniProtKB", "ATCC"
             */
        for (final String rxNormIdentifier : getRxNormIdentifiers(node))
            description.addIdentifier(IdentifierType.RX_NORM_CUI, rxNormIdentifier);
        if (isChemicalADrug(node))
            return new NodeMappingDescription[]{description, describeDrug(node)};
        return new NodeMappingDescription[]{description};
    }

    private boolean isChemicalADrug(final Node node) {
        final String[] types = node.getProperty("types");
        if (types != null)
            for (final String type : types)
                if ("drug".equalsIgnoreCase(type))
                    return true;
        return false;
    }

    private NodeMappingDescription describeDrug(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        description.addIdentifier(IdentifierType.PHARM_GKB, node.<String>getProperty("id"));
        for (final String reference : getCrossReferences(node))
            if (reference.startsWith("DrugBank"))
                description.addIdentifier(IdentifierType.DRUG_BANK, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("KEGG Drug"))
                description.addIdentifier(IdentifierType.KEGG, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("KEGG Compound"))
                description.addIdentifier(IdentifierType.KEGG, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("Chemical Abstracts Service"))
                description.addIdentifier(IdentifierType.CAS, getIdFromPrefixIdPair(reference));
            /*
            "PubChem Compound", "ChEBI", "ChemSpider", "PubChem Substance", "URL", "IUPHAR Ligand", "PDB",
            "Drugs Product Database (DPD)", "Therapeutic Targets Database", "ClinicalTrials.gov",
            "FDA Drug Label at DailyMed", "National Drug Code Directory", "BindingDB", "HET", "HMDB", "GenBank",
            "UniProtKB", "ATCC"
             */
        for (final String rxNormIdentifier : getRxNormIdentifiers(node))
            description.addIdentifier(IdentifierType.RX_NORM_CUI, rxNormIdentifier);
        return description;
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
                description.addIdentifier(IdentifierType.HGNC_ID, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("GeneCard"))
                description.addIdentifier(IdentifierType.GENE_CARD, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("GenAtlas"))
                description.addIdentifier(IdentifierType.GEN_ATLAS, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("Ensembl"))
                description.addIdentifier(IdentifierType.ENSEMBL_GENE_ID, getIdFromPrefixIdPair(reference));
            else if (reference.startsWith("NCBI Gene"))
                description.addIdentifier(IdentifierType.NCBI_GENE, getIdFromPrefixIdPair(reference));
            /*
            "ALFRED", "Comparative Toxicogenomics Database", "GO", "HumanCyc Gene", "ModBase", "MutDB",
            "OMIM", "RefSeq DNA", "RefSeq Protein", "RefSeq RNA", "UCSC Genome Browser", "UniProtKB", "IUPHAR Receptor",
            "URL", "PharmVar Gene", "GenBank"
             */
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
                PharmGKBGraphExporter.CHEMICAL_LABEL, PharmGKBGraphExporter.HAPLOTYPE_LABEL,
                PharmGKBGraphExporter.HAPLOTYPE_SET_LABEL, PharmGKBGraphExporter.GENE_LABEL,
                PharmGKBGraphExporter.VARIANT_LABEL, PharmGKBGraphExporter.PATHWAY_LABEL
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
