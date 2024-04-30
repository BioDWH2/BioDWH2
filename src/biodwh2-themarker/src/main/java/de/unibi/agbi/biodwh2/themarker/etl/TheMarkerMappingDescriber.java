package de.unibi.agbi.biodwh2.themarker.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.core.model.graph.mapping.RNANodeMappingDescription;
import org.apache.commons.lang3.StringUtils;

public class TheMarkerMappingDescriber extends MappingDescriber {
    public TheMarkerMappingDescriber(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public NodeMappingDescription[] describe(final Graph graph, final Node node, final String localMappingLabel) {
        if (TheMarkerGraphExporter.DISEASE_LABEL.equals(localMappingLabel))
            return describeDisease(node);
        if (TheMarkerGraphExporter.DRUG_LABEL.equals(localMappingLabel))
            return describeDrug(node);
        if (TheMarkerGraphExporter.MARKER_LABEL.equals(localMappingLabel))
            return describeMarker(node);
        return null;
    }

    private NodeMappingDescription[] describeDisease(final Node node) {
        final String icd11 = node.getProperty("icd11");
        if (StringUtils.isEmpty(icd11))
            return null;
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addIdentifier(IdentifierType.ICD11, icd11);
        description.addName(node.getProperty("name"));
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeDrug(final Node node) {
        final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.DRUG);
        description.addName(node.getProperty("name"));
        // synonyms
        final Integer pubchemCID = node.getProperty("pubchem_cid");
        if (pubchemCID != null)
            description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, pubchemCID);
        final String drugBankId = node.getProperty("drugbank_id");
        if (drugBankId != null)
            description.addIdentifier(IdentifierType.DRUG_BANK, drugBankId);
        // TODO: final String ttdDrugId = node.getProperty("ttd_drug_id");
        return new NodeMappingDescription[]{description};
    }

    private NodeMappingDescription[] describeMarker(final Node node) {
        final String biomarkerClass = node.getProperty("class");
        // TODO: handle multi marker
        if (biomarkerClass == null || biomarkerClass.contains(";"))
            return null;
        switch (biomarkerClass) {
            case "mRNA":
                return describeRNA(node, RNANodeMappingDescription.RNAType.M_RNA);
            case "lncRNA":
                return describeRNA(node, RNANodeMappingDescription.RNAType.LNC_RNA);
            case "miRNA":
                return describeRNA(node, RNANodeMappingDescription.RNAType.MI_RNA);
            case "Protein": {
                final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.PROTEIN);
                description.addName(node.getProperty("name"));
                description.addNames(node.<String[]>getProperty("synonyms"));
                // TODO: n.gene_ids, n.hgnc_ids, n.ensembl_ids, n.kegg_ids, n.uniprot_ids, n.ttd_target_ids
                return new NodeMappingDescription[]{description};
            }
            case "Gene": {
                final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
                description.addName(node.getProperty("name"));
                description.addNames(node.<String[]>getProperty("synonyms"));
                final var geneIds = node.<Integer[]>getProperty("gene_ids");
                if (geneIds != null && geneIds.length > 0)
                    description.addIdentifier(IdentifierType.NCBI_GENE, geneIds[0]);
                final var hgncIds = node.<String[]>getProperty("hgnc_ids");
                if (hgncIds != null && hgncIds.length > 0)
                    description.addIdentifier(IdentifierType.HGNC_ID,
                                              Integer.parseInt(StringUtils.split(hgncIds[0], ":", 2)[1]));
                final var ensemblIds = node.<String[]>getProperty("ensembl_ids");
                if (ensemblIds != null)
                    for (final String ensemblId : ensemblIds)
                        description.addIdentifier(IdentifierType.ENSEMBL, ensemblId);
                // TODO: n.kegg_ids, n.uniprot_ids
                return new NodeMappingDescription[]{description};
            }
            case "Compound": {
                final var description = new NodeMappingDescription(NodeMappingDescription.NodeType.COMPOUND);
                description.addName(node.getProperty("name"));
                description.addNames(node.<String[]>getProperty("synonyms"));
                final var pubchemCIDs = node.<Integer[]>getProperty("pubchem_cids");
                if (pubchemCIDs != null)
                    for (final Integer pubchemCID : pubchemCIDs)
                        description.addIdentifier(IdentifierType.PUB_CHEM_COMPOUND, pubchemCID);
                return new NodeMappingDescription[]{description};
            }
            // TODO: "Cell", "Other", "Microbiota", "DNA", "Chromosome", "cfDNA", "ctDNA", "mtDNA", "nDNA"
        }
        return null;
    }

    private NodeMappingDescription[] describeRNA(final Node node, final RNANodeMappingDescription.RNAType type) {
        final var description = new RNANodeMappingDescription(type);
        description.addName(node.getProperty("name"));
        description.addNames(node.<String[]>getProperty("synonyms"));
        // TODO: n.gene_ids, n.hgnc_ids, n.ensembl_ids
        // TODO: n.ttd_target_ids, n.mirna_precursor_accessions, n.mirna_mature_accessions
        final var refseqIds = node.<String[]>getProperty("refseq_ids");
        if (refseqIds != null)
            for (final String refseqId : refseqIds)
                description.addIdentifier(IdentifierType.REFSEQ, refseqId);
        return new NodeMappingDescription[]{description};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[] getNodeMappingLabels() {
        return new String[]{
                TheMarkerGraphExporter.DISEASE_LABEL, TheMarkerGraphExporter.DRUG_LABEL,
                TheMarkerGraphExporter.MARKER_LABEL
        };
    }

    @Override
    protected PathMapping[] getEdgePathMappings() {
        return new PathMapping[0];
    }
}
