package de.unibi.agbi.biodwh2.refseq.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.gff.GFF3DataEntry;
import de.unibi.agbi.biodwh2.core.io.gff.GFF3Entry;
import de.unibi.agbi.biodwh2.core.io.gff.GFF3Reader;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.refseq.RefSeqDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RefSeqGraphExporter extends GraphExporter<RefSeqDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefSeqGraphExporter.class);
    static final String ASSEMBLY_LABEL = "Assembly";
    static final String CHROMOSOME_LABEL = "Chromosome";
    static final String GENE_LABEL = "Gene";
    static final String M_RNA_LABEL = "mRNA";
    static final String T_RNA_LABEL = "tRNA";
    static final String R_RNA_LABEL = "rRNA";
    static final String NC_RNA_LABEL = "ncRNA";
    static final String MI_RNA_LABEL = "miRNA";
    static final String SN_RNA_LABEL = "snRNA";
    static final String SC_RNA_LABEL = "scRNA";
    static final String SNO_RNA_LABEL = "snoRNA";
    static final String LNC_RNA_LABEL = "lncRNA";
    static final String VAULT_RNA_LABEL = "vaultRNA";
    static final String Y_RNA_LABEL = "YRNA";
    static final String CDS_LABEL = "CDS";
    static final String PRIMARY_TRANSCRIPT_LABEL = "PrimaryTranscript";

    private final Map<String, String> featureTypeLabelMap = new HashMap<>();
    private final Set<String> unhandledAttributeKeys = new HashSet<>();
    private final Set<String> unhandledLabels = new HashSet<>();
    private final Set<String> unhandledXrefPrefixes = new HashSet<>();

    public RefSeqGraphExporter(final RefSeqDataSource dataSource) {
        super(dataSource);
        featureTypeLabelMap.put("pseudogene", "PseudoGene");
        featureTypeLabelMap.put("gene", GENE_LABEL);
        featureTypeLabelMap.put("sequence_feature", "SequenceFeature");
        featureTypeLabelMap.put("transcript", "Transcript");
        featureTypeLabelMap.put("primary_transcript", PRIMARY_TRANSCRIPT_LABEL);
        featureTypeLabelMap.put("exon", "Exon");
        featureTypeLabelMap.put("mRNA", M_RNA_LABEL);
        featureTypeLabelMap.put("tRNA", T_RNA_LABEL);
        featureTypeLabelMap.put("rRNA", R_RNA_LABEL);
        featureTypeLabelMap.put("Y_RNA", Y_RNA_LABEL);
        featureTypeLabelMap.put("miRNA", MI_RNA_LABEL);
        featureTypeLabelMap.put("snRNA", SN_RNA_LABEL);
        featureTypeLabelMap.put("scRNA", SC_RNA_LABEL);
        featureTypeLabelMap.put("ncRNA", NC_RNA_LABEL);
        featureTypeLabelMap.put("snoRNA", SNO_RNA_LABEL);
        featureTypeLabelMap.put("lnc_RNA", LNC_RNA_LABEL);
        featureTypeLabelMap.put("vault_RNA", VAULT_RNA_LABEL);
        featureTypeLabelMap.put("antisense_RNA", "antisenseRNA");
        featureTypeLabelMap.put("telomerase_RNA", "telomeraseRNA");
        featureTypeLabelMap.put("RNase_MRP_RNA", "RNaseMRPRNA");
        featureTypeLabelMap.put("RNase_P_RNA", "RNasePRNA");
        featureTypeLabelMap.put("CDS", CDS_LABEL);
        featureTypeLabelMap.put("nucleotide_motif", "NucleotideMotif");
        featureTypeLabelMap.put("cDNA_match", "cDNAMatch");
        featureTypeLabelMap.put("match", "Match");
        featureTypeLabelMap.put("biological_region", "BiologicalRegion");
        featureTypeLabelMap.put("enhancer", "Enhancer");
        featureTypeLabelMap.put("silencer", "Silencer");
        featureTypeLabelMap.put("centromere", "Centromere");
        featureTypeLabelMap.put("meiotic_recombination_region", "MeioticRecombinationRegion");
        featureTypeLabelMap.put("tandem_repeat", "TandemRepeat");
        featureTypeLabelMap.put("repeat_region", "RepeatRegion");
        featureTypeLabelMap.put("direct_repeat", "DirectRepeat");
        featureTypeLabelMap.put("dispersed_repeat", "DispersedRepeat");
        featureTypeLabelMap.put("repeat_instability_region", "RepeatInstabilityRegion");
        featureTypeLabelMap.put("epigenetically_modified_region", "EpigeneticallyModifiedRegion");
        featureTypeLabelMap.put("DNaseI_hypersensitive_site", "DNaseIHypersensitiveSite");
        featureTypeLabelMap.put("CAGE_cluster", "CAGECluster");
        featureTypeLabelMap.put("transcriptional_cis_regulatory_region", "TranscriptionalCisRegulatoryRegion");
        featureTypeLabelMap.put("C_gene_segment", "CGeneSegment");
        featureTypeLabelMap.put("D_gene_segment", "DGeneSegment");
        featureTypeLabelMap.put("J_gene_segment", "JGeneSegment");
        featureTypeLabelMap.put("V_gene_segment", "VGeneSegment");
        featureTypeLabelMap.put("insulator", "Insulator");
        featureTypeLabelMap.put("matrix_attachment_site", "MatrixAttachmentSite");
        featureTypeLabelMap.put("regulatory_region", "RegulatoryRegion");
        featureTypeLabelMap.put("response_element", "ResponseElement");
        featureTypeLabelMap.put("sequence_secondary_structure", "SequenceSecondaryStructure");
        featureTypeLabelMap.put("TATA_box", "TATABox");
        featureTypeLabelMap.put("protein_binding_site", "ProteinBindingSite");
        featureTypeLabelMap.put("promoter", "Promoter");
        featureTypeLabelMap.put("locus_control_region", "LocusControlRegion");
        featureTypeLabelMap.put("minisatellite", "MiniSatellite");
        featureTypeLabelMap.put("CAAT_signal", "CAATSignal");
        featureTypeLabelMap.put("microsatellite", "MicroSatellite");
        featureTypeLabelMap.put("mobile_genetic_element", "MobileGeneticElement");
        featureTypeLabelMap.put("enhancer_blocking_element", "EnhancerBlockingElement");
        featureTypeLabelMap.put("conserved_region", "ConservedRegion");
        featureTypeLabelMap.put("sequence_comparison", "SequenceComparison");
        featureTypeLabelMap.put("sequence_alteration", "SequenceAlteration");
        featureTypeLabelMap.put("origin_of_replication", "OriginOfReplication");
        featureTypeLabelMap.put("mitotic_recombination_region", "MitoticRecombinationRegion");
        featureTypeLabelMap.put("recombination_feature", "RecombinationFeature");
        featureTypeLabelMap.put("region", "Region");
        featureTypeLabelMap.put("GC_rich_promoter_region", "GCRichPromoterRegion");
        featureTypeLabelMap.put("replication_regulatory_region", "ReplicationRegulatoryRegion");
        featureTypeLabelMap.put("sequence_alteration_artifact", "SequenceAlterationArtifact");
        featureTypeLabelMap.put("imprinting_control_region", "ImprintingControlRegion");
        featureTypeLabelMap.put("chromosome_breakpoint", "ChromosomeBreakpoint");
        featureTypeLabelMap.put("nucleotide_cleavage_site", "NucleotideCleavageSite");
        featureTypeLabelMap.put("replication_start_site", "ReplicationStartSite");
        featureTypeLabelMap.put("D_loop", "DLoop");
        featureTypeLabelMap.put("non_allelic_homologous_recombination_region",
                                "NonAllelicHomologousRecombinationRegion");
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(ASSEMBLY_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(CHROMOSOME_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        exportFeatures(workspace, graph);
        return true;
    }

    private void exportFeatures(final Workspace workspace, final Graph graph) {
        // TODO: dynamic file name
        final Node assemblyNode = graph.addNode(ASSEMBLY_LABEL, ID_KEY, "GCF_000001405.40", "name", "GRCh38.p14",
                                                "ncbi_taxid", 9606);
        final Map<String, Long> chromosomeNameNodeIdMap = new HashMap<>();
        final Map<String, Long> idNodeIdMap = new HashMap<>();
        try (final InputStream inputStream = FileUtils.openGzip(workspace, dataSource,
                                                                "GCF_000001405.40_GRCh38.p14_genomic.gff.gz");
             final GFF3Reader reader = new GFF3Reader(inputStream, StandardCharsets.UTF_8)) {
            for (final GFF3Entry entry : reader) {
                if (entry instanceof GFF3DataEntry) {
                    final GFF3DataEntry dataEntry = (GFF3DataEntry) entry;
                    final String featureType = dataEntry.getAttribute("gbkey");
                    if ("Src".equals(featureType)) {
                        exportChromosome(graph, assemblyNode, chromosomeNameNodeIdMap, dataEntry);
                    } else {
                        exportFeature(graph, chromosomeNameNodeIdMap, idNodeIdMap, dataEntry);
                    }
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private static void exportChromosome(final Graph graph, final Node assemblyNode,
                                         final Map<String, Long> chromosomeNameNodeIdMap,
                                         final GFF3DataEntry dataEntry) {
        final NodeBuilder builder = graph.buildNode().withLabel(CHROMOSOME_LABEL);
        builder.withProperty(ID_KEY, dataEntry.getSeqId());
        builder.withPropertyIfNotNull("source", dataEntry.getSource());
        builder.withPropertyIfNotNull("name", dataEntry.getAttribute("Name"));
        builder.withPropertyIfNotNull("chromosome", dataEntry.getAttribute("chromosome"));
        builder.withPropertyIfNotNull("mol_type", dataEntry.getAttribute("mol_type"));
        builder.withPropertyIfNotNull("type", dataEntry.getAttribute("genome"));
        builder.withPropertyIfNotNull("start", dataEntry.getStart());
        builder.withPropertyIfNotNull("end", dataEntry.getEnd());
        final Node chromosomeNode = builder.build();
        chromosomeNameNodeIdMap.put(dataEntry.getSeqId(), chromosomeNode.getId());
        graph.addEdge(assemblyNode, chromosomeNode, "HAS");
    }

    private void exportFeature(final Graph graph, final Map<String, Long> chromosomeNameNodeIdMap,
                               final Map<String, Long> idNodeIdMap, final GFF3DataEntry dataEntry) {
        Long nodeId = idNodeIdMap.get(dataEntry.getAttribute("ID"));
        if (nodeId == null)
            nodeId = exportFeatureNode(graph, idNodeIdMap, dataEntry);
        final String parentId = dataEntry.getAttribute("Parent");
        final EdgeBuilder edgeBuilder = graph.buildEdge().withLabel("HAS").toNode(nodeId);
        if (parentId == null)
            edgeBuilder.fromNode(chromosomeNameNodeIdMap.get(dataEntry.getSeqId()));
        else
            edgeBuilder.fromNode(idNodeIdMap.get(parentId));
        edgeBuilder.withPropertyIfNotNull("source", dataEntry.getSource());
        edgeBuilder.withPropertyIfNotNull("start", dataEntry.getStart());
        edgeBuilder.withPropertyIfNotNull("end", dataEntry.getEnd());
        edgeBuilder.withPropertyIfNotNull("strand", dataEntry.getStrand().value);
        edgeBuilder.withPropertyIfNotNull("phase", dataEntry.getPhase());
        edgeBuilder.build();
    }

    private Long exportFeatureNode(final Graph graph, final Map<String, Long> idNodeIdMap,
                                   final GFF3DataEntry dataEntry) {
        final String typeName = dataEntry.getTypeSOName();
        String label = featureTypeLabelMap.get(typeName);
        if (label == null) {
            label = typeName;
            if (!unhandledLabels.contains(typeName)) {
                LOGGER.warn("Unknown label: " + typeName);
                unhandledLabels.add(typeName);
            }
        }
        final String id = dataEntry.getAttribute("ID");
        final NodeBuilder builder = graph.buildNode().withLabel(label);
        builder.withProperty(ID_KEY, id);
        for (final String key : dataEntry.getAttributeKeys())
            addAttributeToFeatureNodeBuilder(dataEntry, builder, key, dataEntry.getAttribute(key));
        final Node node = builder.build();
        final long nodeId = node.getId();
        idNodeIdMap.put(id, nodeId);
        return nodeId;
    }

    private void addAttributeToFeatureNodeBuilder(final GFF3DataEntry dataEntry, final NodeBuilder builder,
                                                  final String key, final String value) {
        final String typeName = dataEntry.getTypeSOName();
        final String gbKey = dataEntry.getAttribute("gbkey");
        switch (key) {
            case "tag":
            case "Name":
            case "Note":
            case "description":
                builder.withPropertyIfNotNull(key.toLowerCase(Locale.ROOT), value);
                break;
            case "Dbxref":
                builder.withPropertyIfNotNull("xrefs", filterXrefsByType(value, typeName, gbKey));
                break;
            case "pseudo":
                // For genes and pseudo-genes, this is already encoded in the label
                if (value != null && !"Gene".equals(gbKey))
                    builder.withPropertyIfNotNull("pseudo", "true".equalsIgnoreCase(value));
                break;
            case "gene_synonym":
                builder.withProperty("gene_synonyms", StringUtils.split(value, ','));
                break;
            // Ignored
            case "transcript_id":
                break;
            // TODO
            case "product":
            case "gene_biotype":
            case "gbkey":
            case "gene":
            case "experiment":
            case "protein_id":
            case "model_evidence":
            case "regulatory_class":
            case "standard_name":
            case "function":
            case "inference":
            case "exception":
            case "feat_class":
            case "rpt_type":
            case "rpt_unit_seq":
            case "rpt_family":
            case "rpt_unit_range":
            case "recombination_class":
            case "anticodon":
            case "transl_except":
            case "partial":
            case "start_range":
            case "end_range":
            case "mobile_element_type":
            case "satellite":
            case "bound_moiety":
            case "bit_score":
            case "blast_score":
            case "filter_score":
            case "matched_bases":
            case "pct_identity_gapopen_only":
            case "pct_identity_ungap":
            case "common_component":
            case "e_value":
            case "weighted_identity":
            case "pct_identity_gap":
            case "Target":
            case "assembly_bases_seq":
            case "for_remapping":
            case "assembly_bases_aln":
            case "pct_coverage_hiqual":
            case "matchable_bases":
            case "pct_coverage":
            case "num_mismatch":
            case "rank":
            case "blast_aligner":
            case "hsp_percent_coverage":
            case "num_ident":
            case "gap_count":
            case "not_for_annotation":
            case "Gap":
            case "consensus_splices":
            case "matches":
            case "product_coverage":
            case "idty":
            case "identity":
            case "exon_identity":
            case "splices":
            case "merge_aligner":
            case "lxr_locAcc_currStat_35":
            case "lxr_locAcc_currStat_120":
            case "direction":
            case "exon_number":
            case "number":
            case "allele":
            case "batch_id":
            case "qtaxid":
            case "crc32":
            case "promoted_rank":
            case "align_id":
            case "curated_alignment":
            case "codons":
            case "transl_table":
                // TODO
                builder.withPropertyIfNotNull(key.toLowerCase(Locale.ROOT), value);
                break;
            default:
                if (!"ID".equals(key) && !"Parent".equals(key) && !unhandledAttributeKeys.contains(key)) {
                    LOGGER.warn("Unknown attribute: " + key);
                    unhandledAttributeKeys.add(key);
                }
                break;
        }
    }

    private String[] filterXrefsByType(final String value, final String typeName, final String gbKey) {
        final List<String> result = new ArrayList<>();
        final boolean isGene = "Gene".equals(gbKey);
        final boolean isMRNA = "mRNA".equals(typeName);
        final boolean isExon = "exon".equals(typeName);
        final boolean isCDS = "CDS".equals(typeName);
        final boolean isPreMiRNA = "precursor_RNA".equals(gbKey);
        final boolean isMiRNA = "miRNA".equals(typeName);
        final boolean isSNP = "sequence_alteration".equals(typeName);
        for (final String xref : StringUtils.split(value, ',')) {
            final String[] xrefParts = StringUtils.split(xref, ":", 2);
            final String prefix = xrefParts[0];
            final String suffix = xrefParts[1];
            switch (prefix) {
                case "GeneID":
                case "MIM":
                case "HGNC":
                    if (isGene)
                        result.add(xref);
                    break;
                case "Genbank":
                    switch (suffix.charAt(1)) {
                        case 'M':
                            if (isMRNA)
                                result.add(xref);
                            break;
                        case 'R':
                            if (!isExon)
                                result.add(xref);
                            break;
                        case 'P':
                            if (isCDS)
                                result.add(xref);
                            break;
                        default:
                            LOGGER.warn("Unhandled Genbank xref type: " + xref);
                            break;
                    }
                    break;
                case "miRBase":
                    if (isPreMiRNA || isMiRNA)
                        result.add(xref);
                    break;
                case "Ensembl":
                    switch (suffix.substring(0, 4)) {
                        case "ENSP":
                            if (isCDS)
                                result.add(xref);
                            break;
                        case "ENST":
                            if (isMRNA)
                                result.add(xref);
                            break;
                        default:
                            LOGGER.warn("Unhandled Ensembl xref type: " + xref);
                            break;
                    }
                    break;
                case "dbSNP":
                    if (isSNP)
                        result.add(xref);
                    break;
                case "CCDS":
                case "RFAM":
                case "VISTA":
                case "IMGT/GENE-DB":
                    // TODO
                    break;
                default:
                    if (!unhandledXrefPrefixes.contains(prefix)) {
                        System.out.println("Unhandled xref prefix '" + prefix + "' (ex: '" + xref + "')");
                        unhandledXrefPrefixes.add(prefix);
                    }
                    break;
            }
        }
        return result.size() > 0 ? result.toArray(new String[0]) : null;
    }
}
