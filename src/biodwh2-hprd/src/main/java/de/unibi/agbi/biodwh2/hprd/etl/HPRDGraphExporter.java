package de.unibi.agbi.biodwh2.hprd.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.fasta.FastaEntry;
import de.unibi.agbi.biodwh2.core.io.fasta.FastaReader;
import de.unibi.agbi.biodwh2.core.model.graph.*;
import de.unibi.agbi.biodwh2.hprd.HPRDDataSource;
import de.unibi.agbi.biodwh2.hprd.model.*;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HPRDGraphExporter extends GraphExporter<HPRDDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(HPRDGraphExporter.class);
    static final String PROTEIN_LABEL = "Protein";
    static final String M_RNA_LABEL = "mRNA";
    static final String GENE_LABEL = "Gene";
    static final String TRANSCRIBES_TO_LABEL = "TRANSCRIBES_TO";
    static final String TRANSLATES_TO_LABEL = "TRANSLATES_TO";
    static final String REFSEQ_ID_KEY = "refseq_id";

    public HPRDGraphExporter(final HPRDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("ProteinComplex", ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, REFSEQ_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(M_RNA_LABEL, REFSEQ_ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        // Ignoring PROTEIN_NOMENCLATURE.txt and GENE_ONTOLOGY.txt
        final Map<String, Gene> genes = new HashMap<>();
        collectGenesFromFastaFiles(workspace, genes);
        collectGenesFromSequenceInformation(workspace, genes);
        collectGenesFromIdMappings(workspace, genes);
        exportGenes(graph, genes);
        exportTissueExpressions(workspace, graph);
        exportGeneticDiseases(workspace, graph);
        exportPostTranslationalModifications(workspace, graph);
        exportProteinArchitectures(workspace, graph);
        exportProteinComplexes(workspace, graph);
        exportProteinProteinInteractions(workspace, graph);
        exportProteinNonProteinInteractions(workspace, graph);
        return true;
    }

    private void collectGenesFromFastaFiles(final Workspace workspace, final Map<String, Gene> genes) {
        for (final FastaEntry sequence : collectRefSeqFastaEntries(workspace, "NUCLEOTIDE_SEQUENCES.txt")) {
            final String[] headerParts = StringUtils.split(sequence.getHeader().substring(1), '|');
            final Gene gene = genes.computeIfAbsent(headerParts[0], (k) -> new Gene(headerParts[0]));
            gene.name = headerParts[3];
            final Transcript transcript = gene.transcripts.computeIfAbsent(headerParts[1],
                                                                           (k) -> new Transcript(headerParts[1]));
            transcript.transcriptRefSeqId = headerParts[2];
            transcript.nucleotideSequence = sequence.getSequence();
        }
        for (final FastaEntry sequence : collectRefSeqFastaEntries(workspace, "PROTEIN_SEQUENCES.txt")) {
            final String[] headerParts = StringUtils.split(sequence.getHeader().substring(1), '|');
            final Gene gene = genes.computeIfAbsent(headerParts[0], (k) -> new Gene(headerParts[0]));
            gene.name = headerParts[3];
            final Transcript transcript = gene.transcripts.computeIfAbsent(headerParts[1],
                                                                           (k) -> new Transcript(headerParts[1]));
            transcript.proteinRefSeqId = headerParts[2];
            transcript.proteinSequence = sequence.getSequence();
        }
    }

    private List<FastaEntry> collectRefSeqFastaEntries(final Workspace workspace, final String fileName) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting {}...", fileName);
        try (final TarArchiveInputStream inputStream = FileUtils.openTarGzip(workspace, dataSource,
                                                                             HPRDUpdater.FILE_NAME)) {
            TarArchiveEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(fileName)) {
                    try (final FastaReader reader = new FastaReader(inputStream, StandardCharsets.UTF_8)) {
                        final List<FastaEntry> result = new ArrayList<>();
                        for (final FastaEntry fastaEntry : reader)
                            result.add(fastaEntry);
                        return result;
                    }
                }
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
        throw new ExporterFormatException("File '" + fileName + "' not found in archive");
    }

    private void collectGenesFromSequenceInformation(final Workspace workspace, final Map<String, Gene> genes) {
        try (final MappingIterator<SequenceInformation> entries = openTsvFile(workspace, "SEQUENCE_INFORMATION.txt",
                                                                              SequenceInformation.class)) {
            while (entries.hasNext()) {
                final SequenceInformation entry = entries.next();
                final Gene gene = genes.computeIfAbsent(entry.hprdId, (k) -> new Gene(entry.hprdId));
                gene.symbol = entry.geneSymbol;
                final Transcript transcript = gene.transcripts.computeIfAbsent(entry.isoformId,
                                                                               (k) -> new Transcript(entry.isoformId));
                transcript.orfStart = entry.orfStart;
                transcript.orfEnd = entry.orfEnd;
                transcript.proteinLength = entry.proteinLength;
                transcript.proteinMolecularWeight = entry.proteinMolecularWeight;
                transcript.proteinRefSeqId = entry.proteinAccession;
                transcript.transcriptRefSeqId = entry.nucleotideAccession;
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private <T> MappingIterator<T> openTsvFile(final Workspace workspace, final String fileName,
                                               Class<T> classType) throws IOException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting {}...", fileName);
        final TarArchiveInputStream inputStream = FileUtils.openTarGzip(workspace, dataSource, HPRDUpdater.FILE_NAME);
        TarArchiveEntry entry;
        while ((entry = inputStream.getNextEntry()) != null)
            if (entry.getName().endsWith(fileName))
                return FileUtils.openSeparatedValuesFile(inputStream, classType, '\t', false, false);
        throw new ExporterFormatException("File '" + fileName + "' not found in archive");
    }

    private void collectGenesFromIdMappings(final Workspace workspace, final Map<String, Gene> genes) {
        try (final MappingIterator<HPRDIdMapping> entries = openTsvFile(workspace, "HPRD_ID_MAPPINGS.txt",
                                                                        HPRDIdMapping.class)) {
            while (entries.hasNext()) {
                final HPRDIdMapping entry = entries.next();
                final Gene gene = genes.computeIfAbsent(entry.hprdId, (k) -> new Gene(entry.hprdId));
                gene.symbol = gene.symbol != null ? gene.symbol : entry.geneSymbol;
                gene.entrezGeneId = gene.entrezGeneId != null ? gene.entrezGeneId : getIntIdOrNull(entry.entrezGeneId);
                gene.omimId = gene.omimId != null ? gene.omimId : getIntIdOrNull(entry.omimId);
                gene.name = gene.name != null ? gene.name : (nullifyDashValue(entry.mainName));
                gene.swissProtIds = "-".equals(entry.swissProtId) ? null : Arrays.stream(
                        StringUtils.split(entry.swissProtId, ',')).filter(StringUtils::isNotEmpty).toArray(
                        String[]::new);
            }
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private String nullifyDashValue(final String value) {
        return "-".equals(value) ? null : value;
    }

    private Integer getIntIdOrNull(final String value) {
        return value != null && !"-".equals(value) ? Integer.parseInt(value) : null;
    }

    private void exportGenes(final Graph graph, final Map<String, Gene> genes) {
        for (final Gene gene : genes.values()) {
            final NodeBuilder geneBuilder = graph.buildNode().withLabel(GENE_LABEL);
            geneBuilder.withProperty(ID_KEY, gene.id);
            geneBuilder.withPropertyIfNotNull("symbol", nullifyDashValue(gene.symbol));
            geneBuilder.withPropertyIfNotNull("name", gene.name);
            geneBuilder.withPropertyIfNotNull("entrez_gene_id", gene.entrezGeneId);
            geneBuilder.withPropertyIfNotNull("omim_id", gene.omimId);
            geneBuilder.withPropertyIfNotNull("swissprot_id", gene.swissProtIds);
            gene.nodeId = geneBuilder.build().getId();
            for (final Transcript transcript : gene.transcripts.values()) {
                // Create transcript node
                final NodeBuilder transcriptBuilder = graph.buildNode().withLabel(M_RNA_LABEL);
                transcriptBuilder.withProperty(REFSEQ_ID_KEY, transcript.transcriptRefSeqId);
                transcriptBuilder.withPropertyIfNotNull("sequence", transcript.nucleotideSequence);
                transcript.transcriptNodeId = transcriptBuilder.build().getId();
                // Create protein node
                final NodeBuilder proteinBuilder = graph.buildNode().withLabel(PROTEIN_LABEL);
                proteinBuilder.withProperty(ID_KEY, transcript.isoformId);
                proteinBuilder.withProperty(REFSEQ_ID_KEY, transcript.proteinRefSeqId);
                proteinBuilder.withPropertyIfNotNull("sequence", transcript.proteinSequence);
                proteinBuilder.withPropertyIfNotNull("length", transcript.proteinLength);
                proteinBuilder.withPropertyIfNotNull("molecular_weight", transcript.proteinMolecularWeight);
                transcript.proteinNodeId = proteinBuilder.build().getId();
                // Connect nodes
                graph.addEdge(gene.nodeId, transcript.transcriptNodeId, TRANSCRIBES_TO_LABEL, "orf_start",
                              transcript.orfStart, "orf_end", transcript.orfEnd);
                graph.addEdge(transcript.transcriptNodeId, transcript.proteinNodeId, TRANSLATES_TO_LABEL);
            }
        }
    }

    private void exportTissueExpressions(final Workspace workspace, final Graph graph) {
        final Map<String, Long> tissueNodeIdMap = new HashMap<>();
        try (final MappingIterator<TissueExpression> entries = openTsvFile(workspace, "TISSUE_EXPRESSIONS.txt",
                                                                           TissueExpression.class)) {
            while (entries.hasNext())
                exportTissueExpression(graph, entries.next(), tissueNodeIdMap);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportTissueExpression(final Graph graph, final TissueExpression entry,
                                        final Map<String, Long> tissueNodeIdMap) {
        if ("-".equals(entry.refSeqId)) {
            LOGGER.warn("Skipping tissue expression entry with unknown RefSeq Id ({})", entry.hprdId);
            return;
        }
        final Node proteinNode = graph.findNode(PROTEIN_LABEL, REFSEQ_ID_KEY, entry.refSeqId);
        Long tissueNodeId = tissueNodeIdMap.get(entry.expressionTerm);
        if (tissueNodeId == null) {
            tissueNodeId = graph.addNode("Tissue", "name", entry.expressionTerm).getId();
            tissueNodeIdMap.put(entry.expressionTerm, tissueNodeId);
        }
        final Integer[] pubmedIds = convertPubmedIds(entry.referenceId);
        graph.addEdge(proteinNode, tissueNodeId, "EXPRESSED_IN", "status", entry.status, "pubmed_ids", pubmedIds);
    }

    private Integer[] convertPubmedIds(final String value) {
        if ("-".equals(value))
            return null;
        try {
            return Arrays.stream(StringUtils.split(value, ',')).filter(StringUtils::isNotEmpty).filter(
                    StringUtils::isNumeric).map(Integer::parseInt).toArray(Integer[]::new);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void exportGeneticDiseases(final Workspace workspace, final Graph graph) {
        final Map<String, Long> diseaseNodeIdMap = new HashMap<>();
        try (final MappingIterator<GeneticDisease> entries = openTsvFile(workspace, "GENETIC_DISEASES.txt",
                                                                         GeneticDisease.class)) {
            while (entries.hasNext())
                exportGeneticDisease(graph, entries.next(), diseaseNodeIdMap);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportGeneticDisease(final Graph graph, final GeneticDisease entry,
                                      final Map<String, Long> diseaseNodeIdMap) {
        if ("-".equals(entry.refSeqId)) {
            LOGGER.warn("Skipping genetic disease entry with unknown RefSeq Id ({})", entry.hprdId);
            return;
        }
        final Node proteinNode = graph.findNode(PROTEIN_LABEL, REFSEQ_ID_KEY, entry.refSeqId);
        Long diseaseNodeId = diseaseNodeIdMap.get(entry.diseaseName);
        if (diseaseNodeId == null) {
            diseaseNodeId = graph.addNode("Disease", "name", entry.diseaseName).getId();
            diseaseNodeIdMap.put(entry.diseaseName, diseaseNodeId);
        }
        final Integer[] pubmedIds = convertPubmedIds(entry.referenceId);
        graph.addEdge(proteinNode, diseaseNodeId, "ASSOCIATED_WITH", "pubmed_ids", pubmedIds);
    }

    private void exportPostTranslationalModifications(final Workspace workspace, final Graph graph) {
        try (final MappingIterator<PostTranslationalModification> entries = openTsvFile(workspace,
                                                                                        "POST_TRANSLATIONAL_MODIFICATIONS.txt",
                                                                                        PostTranslationalModification.class)) {
            while (entries.hasNext())
                exportPostTranslationalModification(graph, entries.next());
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportPostTranslationalModification(final Graph graph, final PostTranslationalModification entry) {
        if ("-".equals(entry.substrateRefSeqId)) {
            LOGGER.warn("Skipping post translational modification entry with unknown RefSeq Id ({})",
                        entry.substrateHprdId);
            return;
        }
        final Node proteinNode = graph.findNode(PROTEIN_LABEL, REFSEQ_ID_KEY, entry.substrateRefSeqId);
        final NodeBuilder builder = graph.buildNode().withLabel("PostTranslationalModification");
        builder.withPropertyIfNotNull("site", nullifyDashValue(entry.site));
        builder.withPropertyIfNotNull("residue", nullifyDashValue(entry.residue));
        builder.withPropertyIfNotNull("type", nullifyDashValue(entry.modificationType));
        builder.withPropertyIfNotNull("experiment_types", "-".equals(entry.experimentType) ? null :
                                                          StringUtils.split(entry.experimentType, ";"));
        builder.withPropertyIfNotNull("pubmed_ids", convertPubmedIds(entry.referenceId));
        final Node modificationNode = builder.build();
        graph.addEdge(proteinNode, modificationNode, "SUBSTRATE_OF");
        if (entry.enzymeHprdId != null && !"-".equals(entry.enzymeHprdId)) {
            final Node enzymeNode = graph.findNode(GENE_LABEL, ID_KEY, entry.enzymeHprdId);
            graph.addEdge(enzymeNode, modificationNode, "ENZYME_FOR");
        }
    }

    private void exportProteinArchitectures(final Workspace workspace, final Graph graph) {
        final Map<String, Map<String, Long>> typeNameNodeIdMap = new HashMap<>();
        try (final MappingIterator<ProteinArchitecture> entries = openTsvFile(workspace, "PROTEIN_ARCHITECTURE.txt",
                                                                              ProteinArchitecture.class)) {
            while (entries.hasNext())
                exportProteinArchitecture(graph, entries.next(), typeNameNodeIdMap);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportProteinArchitecture(final Graph graph, final ProteinArchitecture entry,
                                           final Map<String, Map<String, Long>> typeNameNodeIdMap) {
        if ("-".equals(entry.refSeqId)) {
            LOGGER.warn("Skipping protein architecture entry with unknown RefSeq Id ({})", entry.hprdId);
            return;
        }
        final Node proteinNode = graph.findNode(PROTEIN_LABEL, REFSEQ_ID_KEY, entry.refSeqId);
        final Map<String, Long> nameNodeIdMap = typeNameNodeIdMap.computeIfAbsent(entry.architectureType,
                                                                                  (k) -> new HashMap<>());
        if (StringUtils.isEmpty(entry.architectureType)) {
            LOGGER.warn("Skipping protein architecture entry with unknown architecture type ({}, {})", entry.hprdId,
                        entry.refSeqId);
            return;
        }
        Long architectureNodeId = nameNodeIdMap.get(entry.architectureName);
        if (architectureNodeId == null) {
            architectureNodeId = graph.addNode(entry.architectureType, "name", entry.architectureName).getId();
            nameNodeIdMap.put(entry.architectureName, architectureNodeId);
        }
        final EdgeBuilder builder = graph.buildEdge().withLabel("HAS").fromNode(proteinNode).toNode(architectureNodeId);
        builder.withPropertyIfNotNull("start_site", entry.startSite);
        String endSite = entry.endSite;
        String referenceType = entry.referenceType;
        String referenceId = entry.referenceId;
        // Some entries are corrupt with additional tab characters somewhere between the last 4 columns.
        // We try to eliminate them using two overflow helper columns in the model.
        if (entry.overflowHelper1 != null || entry.overflowHelper2 != null) {
            List<String> overflowCandidates = new ArrayList<>();
            overflowCandidates.add(entry.endSite);
            overflowCandidates.add(entry.referenceType);
            overflowCandidates.add(entry.referenceId);
            overflowCandidates.add(entry.overflowHelper1);
            overflowCandidates.add(entry.overflowHelper2);
            for (int i = overflowCandidates.size() - 1; i >= 0; i--)
                if (overflowCandidates.get(i) == null)
                    overflowCandidates.remove(i);
            endSite = overflowCandidates.get(0);
            referenceType = overflowCandidates.get(1);
            referenceId = overflowCandidates.get(2);
        }
        builder.withPropertyIfNotNull("end_site", endSite == null ? null : Integer.parseInt(endSite.trim()));
        builder.withPropertyIfNotNull("reference_type", nullifyDashValue(referenceType));
        builder.withPropertyIfNotNull("reference_id", nullifyDashValue(referenceId));
        builder.build();
    }

    private void exportProteinComplexes(final Workspace workspace, final Graph graph) {
        try (final MappingIterator<ProteinComplex> entries = openTsvFile(workspace, "PROTEIN_COMPLEXES.txt",
                                                                         ProteinComplex.class)) {
            while (entries.hasNext())
                exportProteinComplex(graph, entries.next());
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportProteinComplex(final Graph graph, final ProteinComplex entry) {
        if ("None".equals(entry.interactorHprdId))
            return;
        if ("-".equals(entry.interactorRefSeqId)) {
            LOGGER.warn("Skipping protein complex entry with unknown RefSeq Id ({})", entry.interactorHprdId);
            return;
        }
        final Node proteinNode = graph.findNode(PROTEIN_LABEL, REFSEQ_ID_KEY, entry.interactorRefSeqId);
        Node complexNode = graph.findNode("ProteinComplex", ID_KEY, entry.hprdInteractionId);
        if (complexNode == null)
            complexNode = graph.addNode("ProteinComplex", ID_KEY, entry.hprdInteractionId);
        graph.addEdge(complexNode, proteinNode, "HAS", "experiment_types", StringUtils.split(entry.experimentType, ';'),
                      "pubmed_ids", convertPubmedIds(entry.referenceId));
    }

    private void exportProteinProteinInteractions(final Workspace workspace, final Graph graph) {
        try (final MappingIterator<BinaryProteinProteinInteraction> entries = openTsvFile(workspace,
                                                                                          "BINARY_PROTEIN_PROTEIN_INTERACTIONS.txt",
                                                                                          BinaryProteinProteinInteraction.class)) {
            while (entries.hasNext())
                exportProteinProteinInteraction(graph, entries.next());
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportProteinProteinInteraction(final Graph graph, final BinaryProteinProteinInteraction entry) {
        if ("-".equals(entry.interactor1RefSeqId) || "-".equals(entry.interactor2RefSeqId)) {
            LOGGER.warn("Skipping protein protein interaction entry with unknown RefSeq Id ({}, {})",
                        entry.interactor1HprdId, entry.interactor2HprdId);
            return;
        }
        final Node protein1Node = graph.findNode(PROTEIN_LABEL, REFSEQ_ID_KEY, entry.interactor1RefSeqId);
        final Node protein2Node = graph.findNode(PROTEIN_LABEL, REFSEQ_ID_KEY, entry.interactor2RefSeqId);
        graph.addEdge(protein1Node, protein2Node, "INTERACTS_WITH", "experiment_types",
                      StringUtils.split(entry.experimentType, ';'), "pubmed_ids", convertPubmedIds(entry.referenceId));
    }

    private void exportProteinNonProteinInteractions(final Workspace workspace, final Graph graph) {
        final Map<String, Long> targetNodeIdMap = new HashMap<>();
        try (final MappingIterator<BinaryProteinNonProteinInteraction> entries = openTsvFile(workspace,
                                                                                             "BINARY_PROTEIN_NONPROTEIN_INTERACTIONS.txt",
                                                                                             BinaryProteinNonProteinInteraction.class)) {
            while (entries.hasNext())
                exportProteinNonProteinInteraction(graph, entries.next(), targetNodeIdMap);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportProteinNonProteinInteraction(final Graph graph, final BinaryProteinNonProteinInteraction entry,
                                                    final Map<String, Long> targetNodeIdMap) {
        if ("-".equals(entry.interactorRefSeqId)) {
            LOGGER.warn("Skipping protein non-protein interaction entry with unknown RefSeq Id ({})",
                        entry.interactorHprdId);
            return;
        }
        if (StringUtils.isBlank(entry.nonProteinInteractorName))
            return;
        final Node proteinNode = graph.findNode(PROTEIN_LABEL, REFSEQ_ID_KEY, entry.interactorRefSeqId);
        Long targetNodeId = targetNodeIdMap.get(entry.nonProteinInteractorName);
        if (targetNodeId == null) {
            targetNodeId = graph.addNode("Interactor", "name", entry.nonProteinInteractorName).getId();
            targetNodeIdMap.put(entry.nonProteinInteractorName, targetNodeId);
        }
        graph.addEdge(proteinNode, targetNodeId, "INTERACTS_WITH", "experiment_types",
                      StringUtils.split(entry.experimentType, ';'), "pubmed_ids", convertPubmedIds(entry.referenceId));
    }
}
