package de.unibi.agbi.biodwh2.mirbase.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.fasta.FastaEntry;
import de.unibi.agbi.biodwh2.core.io.fasta.FastaReader;
import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileEntry;
import de.unibi.agbi.biodwh2.core.io.flatfile.FlatFileReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.mirbase.MiRBaseDataSource;
import de.unibi.agbi.biodwh2.mirbase.model.*;
import de.unibi.agbi.biodwh2.mirbase.utils.AlignmentUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiRBaseGraphExporter extends GraphExporter<MiRBaseDataSource> {
    private static final Logger LOGGER = LogManager.getLogger(MiRBaseGraphExporter.class);
    private static final Pattern MIRNA_ACCESSION_PATTERN = Pattern.compile("MI[0-9]+");
    private static final Pattern MATURE_ACCESSION_PATTERN = Pattern.compile("MIMAT[0-9]+");
    public static final String PRE_MI_RNA_LABEL = "pre_miRNA";
    public static final String MI_RNA_LABEL = "miRNA";
    public static final String SPECIES_LABEL = "Species";
    public static final String GENE_LABEL = "Gene";
    public static final String FAMILY_LABEL = "Family";
    public static final String REFERENCE_LABEL = "Reference";

    public MiRBaseGraphExporter(final MiRBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 3;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(PRE_MI_RNA_LABEL, "accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(MI_RNA_LABEL, "accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(FAMILY_LABEL, "accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(SPECIES_LABEL, "ncbi_taxid", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(REFERENCE_LABEL, "pmid", IndexDescription.Type.UNIQUE));
        logStep(1, "species");
        final Map<Long, Long> speciesIdNodeIdMap = exportSpecies(workspace, graph);
        logStep(2, "pre_miRNAs");
        final Map<Long, Long> mirnaIdNodeIdMap = exportMirnas(workspace, graph, speciesIdNodeIdMap);
        logStep(3, "miRNAs");
        final Map<Long, Long> matureIdNodeIdMap = exportMirnaMatures(workspace, graph, mirnaIdNodeIdMap);
        logStep(4, "miRNA relations");
        exportMirnaMatureRelations(workspace, graph, mirnaIdNodeIdMap, matureIdNodeIdMap);
        logStep(5, "families");
        exportFamilies(workspace, graph, mirnaIdNodeIdMap);
        logStep(6, "references");
        exportReferences(workspace, graph);
        logStep(7, "confidences");
        exportConfidences(workspace, graph, mirnaIdNodeIdMap);
        logStep(8, "contexts");
        exportContexts(workspace, graph, mirnaIdNodeIdMap);
        return true;
    }

    private void logStep(final int step, final String name) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("({}/8) Exporting {}...", step, name);
    }

    private Map<Long, Long> exportSpecies(final Workspace workspace, final Graph graph) {
        final Map<Long, Long> idNodeIdMap = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_species.txt", MirnaSpecies.class, (entry) -> {
                final var taxonId = "\\N".equals(entry.taxonId) ? null : Integer.parseInt(entry.taxonId);
                if (speciesFilter.isSpeciesAllowed(taxonId))
                    idNodeIdMap.put(entry.autoId, graph.addNodeFromModel(entry).getId());
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_species.txt'", e);
        }
        return idNodeIdMap;
    }

    private Map<Long, Long> exportMirnas(final Workspace workspace, final Graph graph,
                                         final Map<Long, Long> speciesIdNodeIdMap) {
        final Map<String, String> sequenceMap = getMirnaSequenceMap(workspace);
        final Map<String, String> alignmentMap = getMirnaAlignmentMap(workspace);
        final Map<Long, Integer> confidenceMap = getMirnaConfidenceScoreMap(workspace);
        final Map<Long, MirnaChromosomeBuild> chromosomeBuildMap = getMirnaChromosomeBuildMap(workspace);
        final Map<Long, Set<String>> xrefsMap = getMirnaXrefMap(workspace);
        final Map<Long, Long> idNodeIdMap = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna.txt", Mirna.class, (entry) -> {
                if (entry.deadFlag != 0)
                    return;
                final Long speciesNodeId = speciesIdNodeIdMap.get(entry.autoSpecies);
                if (speciesNodeId == null)
                    return;
                final NodeBuilder builder = graph.buildNode().withLabel(PRE_MI_RNA_LABEL).withModel(entry);
                final Integer confidence = confidenceMap.get(entry.autoId);
                if (confidence != null)
                    builder.withProperty("confidence", confidence > 0);
                final MirnaChromosomeBuild build = chromosomeBuildMap.get(entry.autoId);
                if (build != null) {
                    builder.withPropertyIfNotNull("xsome", build.xsome);
                    builder.withPropertyIfNotNull("contig_start", build.contigStart);
                    builder.withPropertyIfNotNull("contig_end", build.contigEnd);
                    builder.withPropertyIfNotNull("strand", build.strand);
                }
                final Set<String> xrefs = xrefsMap.get(entry.autoId);
                if (xrefs != null)
                    builder.withProperty("xrefs", xrefs.toArray(new String[0]));
                final String sequence = sequenceMap.get(entry.mirnaAcc);
                if (sequence != null)
                    builder.withProperty("sequence", sequence);
                final String alignment = alignmentMap.get(entry.mirnaId);
                if (alignment != null) {
                    builder.withProperty("alignment", StringUtils.split(alignment, '\n'));
                    builder.withPropertyIfNotNull("fold", AlignmentUtils.getFoldStringFromHairpinAlignment(alignment));
                }
                final Node node = builder.build();
                idNodeIdMap.put(entry.autoId, node.getId());
                graph.addEdge(node, speciesNodeId, "BELONGS_TO");
                exportMirnaGene(graph, xrefs, node);
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna.txt'", e);
        }
        return idNodeIdMap;
    }

    private Map<String, String> getMirnaSequenceMap(final Workspace workspace) {
        final Map<String, String> result = new HashMap<>();
        try (final var reader = new FastaReader(dataSource.resolveSourceFilePath(workspace, "hairpin.fa"),
                                                StandardCharsets.UTF_8)) {
            for (final FastaEntry entry : reader) {
                final Matcher idMatcher = MIRNA_ACCESSION_PATTERN.matcher(entry.getHeader());
                if (idMatcher.find()) {
                    final String accession = idMatcher.group(0);
                    result.put(accession, entry.getSequence());
                }
            }
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to parse the file 'hairpin.fa'", e);
        }
        return result;
    }

    private Map<String, String> getMirnaAlignmentMap(final Workspace workspace) {
        final Map<String, String> result = new HashMap<>();
        try (final var reader = new FastaReader(dataSource.resolveSourceFilePath(workspace, "miRNA.str"),
                                                StandardCharsets.UTF_8, false)) {
            for (final FastaEntry entry : reader) {
                final String id = StringUtils.split(entry.getHeader(), " ")[0].substring(1);
                result.put(id, entry.getSequence());
            }
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to parse the file 'hairpin.fa'", e);
        }
        return result;
    }

    private Map<Long, Integer> getMirnaConfidenceScoreMap(final Workspace workspace) {
        final Map<Long, Integer> result = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "confidence_score.txt", ConfidenceScore.class,
                              (entry) -> result.put(entry.autoMirna, entry.confidence));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'confidence_score.txt'", e);
        }
        return result;
    }

    private Map<Long, MirnaChromosomeBuild> getMirnaChromosomeBuildMap(final Workspace workspace) {
        final Map<Long, MirnaChromosomeBuild> result = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_chromosome_build.txt", MirnaChromosomeBuild.class,
                              (entry) -> result.put(entry.autoMirna, entry));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_chromosome_build.txt'", e);
        }
        return result;
    }

    private Map<Long, Set<String>> getMirnaXrefMap(final Workspace workspace) {
        final Map<Long, String> dbIdPrefixMap = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_database_url.txt", MirnaDatabaseUrl.class,
                              (entry) -> dbIdPrefixMap.put(entry.autoDb, entry.displayName));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_database_url.txt'", e);
        }
        final Map<Long, Set<String>> result = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_database_links.txt", MirnaDatabaseLink.class, (entry) -> {
                final Set<String> xrefs = result.computeIfAbsent(entry.autoMirna, k -> new HashSet<>());
                xrefs.add(dbIdPrefixMap.get(entry.autoDb) + ':' + entry.link);
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_database_links.txt'", e);
        }
        return result;
    }

    private void exportMirnaGene(final Graph graph, final Set<String> xrefs, final Node mirnaNode) {
        if (xrefs == null)
            return;
        Integer hgncId = null;
        Integer entrezId = null;
        for (final String xref : xrefs) {
            final String[] parts = StringUtils.split(xref, ":", 2);
            if ("HGNC".equals(parts[0]))
                hgncId = Integer.parseInt(parts[1]);
            else if ("EntrezGene".equals(parts[0]))
                entrezId = Integer.parseInt(parts[1]);
        }
        Node node = null;
        if (hgncId != null && entrezId != null)
            node = graph.addNode(GENE_LABEL, "hgnc_id", hgncId, "entrez_gene_id", entrezId);
        else if (hgncId != null)
            node = graph.addNode(GENE_LABEL, "hgnc_id", hgncId);
        else if (entrezId != null)
            node = graph.addNode(GENE_LABEL, "entrez_gene_id", entrezId);
        if (node != null)
            graph.addEdge(node, mirnaNode, "TRANSCRIBES_TO");
    }

    private Map<Long, Long> exportMirnaMatures(final Workspace workspace, final Graph graph,
                                               final Map<Long, Long> mirnaIdNodeIdMap) {
        final Set<Long> usedAutoMatures = getUsedAutoMatures(workspace, mirnaIdNodeIdMap);
        final Map<String, String> sequenceMap = getMatureSequenceMap(workspace);
        final Map<Long, Set<String>> xrefsMap = getMatureXrefMap(workspace);
        final Map<Long, Long> idNodeIdMap = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_mature.txt", MirnaMature.class, (entry) -> {
                if (entry.deadFlag != 0 || !usedAutoMatures.contains(entry.autoId))
                    return;
                final NodeBuilder builder = graph.buildNode().withLabel(MI_RNA_LABEL).withModel(entry);
                final Set<String> xrefs = xrefsMap.get(entry.autoId);
                if (xrefs != null)
                    builder.withProperty("xrefs", xrefs.toArray(new String[0]));
                final String sequence = sequenceMap.get(entry.matureAcc);
                if (sequence != null)
                    builder.withProperty("sequence", sequence);
                final Node node = builder.build();
                idNodeIdMap.put(entry.autoId, node.getId());
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_mature.txt'", e);
        }
        return idNodeIdMap;
    }

    private Set<Long> getUsedAutoMatures(final Workspace workspace, final Map<Long, Long> mirnaIdNodeIdMap) {
        final Set<Long> usedAutoMature = new HashSet<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_pre_mature.txt", MirnaPreMature.class, (entry) -> {
                final Long mirnaNodeId = mirnaIdNodeIdMap.get(entry.autoMirna);
                if (mirnaNodeId != null)
                    usedAutoMature.add(entry.autoMature);
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_pre_mature.txt'", e);
        }
        return usedAutoMature;
    }

    private Map<String, String> getMatureSequenceMap(final Workspace workspace) {
        final Map<String, String> result = new HashMap<>();
        try (final var reader = new FastaReader(dataSource.resolveSourceFilePath(workspace, "mature.fa"),
                                                StandardCharsets.UTF_8)) {
            for (final FastaEntry entry : reader) {
                final Matcher idMatcher = MATURE_ACCESSION_PATTERN.matcher(entry.getHeader());
                if (idMatcher.find()) {
                    final String accession = idMatcher.group(0);
                    result.put(accession, entry.getSequence());
                }
            }
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to parse the file 'mature.fa'", e);
        }
        return result;
    }

    private Map<Long, Set<String>> getMatureXrefMap(final Workspace workspace) {
        final Map<Long, String> dbIdPrefixMap = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mature_database_url.txt", MatureDatabaseUrl.class, (entry) -> {
                dbIdPrefixMap.put(entry.autoDb, entry.displayName);
                // TODO: entry.type [0: Database links, 1: Predicted targets, 2: ?]
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mature_database_url.txt'", e);
        }
        final Map<Long, Set<String>> result = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mature_database_links.txt", MatureDatabaseLink.class, (entry) -> {
                final Set<String> xrefs = result.computeIfAbsent(entry.autoMature, k -> new HashSet<>());
                xrefs.add(dbIdPrefixMap.get(entry.autoDb) + ':' + entry.link);
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mature_database_links.txt'", e);
        }
        return result;
    }

    private void exportMirnaMatureRelations(final Workspace workspace, final Graph graph,
                                            final Map<Long, Long> mirnaIdNodeIdMap,
                                            final Map<Long, Long> matureIdNodeIdMap) {
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_pre_mature.txt", MirnaPreMature.class, (entry) -> {
                final Long mirnaNodeId = mirnaIdNodeIdMap.get(entry.autoMirna);
                final Long matureNodeId = matureIdNodeIdMap.get(entry.autoMature);
                if (mirnaNodeId != null && matureNodeId != null)
                    graph.addEdge(mirnaNodeId, matureNodeId, "CLEAVES_TO", "from", entry.matureFrom, "to",
                                  entry.matureTo);
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_pre_mature.txt'", e);
        }
    }

    private void exportFamilies(final Workspace workspace, final Graph graph, final Map<Long, Long> mirnaIdNodeIdMap) {
        final Set<Long> usedAutoPrefam = new HashSet<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_2_prefam.txt", Mirna2Prefam.class, (entry) -> {
                final Long mirnaNodeId = mirnaIdNodeIdMap.get(entry.autoMirna);
                if (mirnaNodeId != null)
                    usedAutoPrefam.add(entry.autoPrefam);
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_2_prefam.txt'", e);
        }
        final Map<Long, Long> prefamIdNodeIdMap = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_prefam.txt", MirnaPrefam.class, (entry) -> {
                if (usedAutoPrefam.contains(entry.autoPrefam)) {
                    final Node node = graph.addNodeFromModel(entry);
                    prefamIdNodeIdMap.put(entry.autoPrefam, node.getId());
                }
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_prefam.txt'", e);
        }
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_2_prefam.txt", Mirna2Prefam.class, (entry) -> {
                final Long mirnaNodeId = mirnaIdNodeIdMap.get(entry.autoMirna);
                final Long familyNodeId = prefamIdNodeIdMap.get(entry.autoPrefam);
                if (mirnaNodeId != null && familyNodeId != null)
                    graph.addEdge(mirnaNodeId, familyNodeId, "BELONGS_TO");
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_2_prefam.txt'", e);
        }
    }

    private void exportReferences(final Workspace workspace, final Graph graph) {
        final Map<String, Long> referenceKeyNodeIdMap = new HashMap<>();
        try (final var reader = new FlatFileReader(dataSource.resolveSourceFilePath(workspace, "miRNA.dat"),
                                                   StandardCharsets.UTF_8)) {
            for (final FlatFileEntry entry : reader) {
                final String mirnaAccession = StringUtils.strip(entry.getProperty("AC").value, " ;");
                final Node mirnaNode = graph.findNode(PRE_MI_RNA_LABEL, "accession", mirnaAccession);
                if (mirnaNode == null)
                    continue;
                final List<List<FlatFileEntry.KeyValuePair>> references = entry.getComplexProperties("RN");
                for (final List<FlatFileEntry.KeyValuePair> reference : references) {
                    Integer index = null;
                    Integer pmid = null;
                    Integer medline = null;
                    String title = null;
                    String authors = null;
                    String journal = null;
                    for (final FlatFileEntry.KeyValuePair pair : reference) {
                        switch (pair.key) {
                            case "RN":
                                index = Integer.parseInt(StringUtils.strip(pair.value, "][;\"").trim());
                                break;
                            case "RX":
                                final String[] parts = StringUtils.split(pair.value, ";.");
                                if ("PUBMED".equals(parts[0]))
                                    pmid = Integer.parseInt(parts[1].trim());
                                else if ("MEDLINE".equals(parts[0]))
                                    medline = Integer.parseInt(parts[1].trim());
                                break;
                            case "RA":
                                authors = StringUtils.strip(StringUtils.replace(pair.value, "\n", " "), ";\"");
                                break;
                            case "RT":
                                title = StringUtils.strip(StringUtils.replace(pair.value, "\n", " "), ";\"");
                                break;
                            case "RL":
                                journal = StringUtils.replace(pair.value, "\n", " ");
                                break;
                        }
                    }
                    final String referenceKey = medline + "|" + pmid + "|" + title + "|" + authors + "|" + journal;
                    Long nodeId = referenceKeyNodeIdMap.get(referenceKey);
                    if (nodeId == null) {
                        final NodeBuilder builder = graph.buildNode().withLabel(REFERENCE_LABEL);
                        builder.withPropertyIfNotNull("medline", medline);
                        builder.withPropertyIfNotNull("pmid", pmid);
                        builder.withPropertyIfNotNull("title", title);
                        builder.withPropertyIfNotNull("authors", authors);
                        builder.withPropertyIfNotNull("journal", journal);
                        nodeId = builder.build().getId();
                        referenceKeyNodeIdMap.put(referenceKey, nodeId);
                    }
                    graph.addEdge(mirnaNode, nodeId, "REFERENCES", "index", index);
                }
            }
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to parse the file 'miRNA.dat'", e);
        }
    }

    private void exportConfidences(final Workspace workspace, final Graph graph,
                                   final Map<Long, Long> mirnaIdNodeIdMap) {
        try {
            FileUtils.openTsv(workspace, dataSource, "confidence.txt", Confidence.class, (entry) -> {
                final Long mirnaNodeId = mirnaIdNodeIdMap.get(entry.autoMirna);
                if (mirnaNodeId != null) {
                    final Node node = graph.addNodeFromModel(entry);
                    graph.addEdge(mirnaNodeId, node, "HAS_CONFIDENCE");
                }
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'confidence.txt'", e);
        }
    }

    private void exportContexts(final Workspace workspace, final Graph graph, final Map<Long, Long> mirnaIdNodeIdMap) {
        try {
            FileUtils.openTsv(workspace, dataSource, "mirna_context.txt", MirnaContext.class, (entry) -> {
                final Long mirnaNodeId = mirnaIdNodeIdMap.get(entry.autoMirna);
                if (mirnaNodeId != null) {
                    final Node node = graph.addNodeFromModel(entry);
                    graph.addEdge(mirnaNodeId, node, "HAS_CONTEXT");
                }
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to parse the file 'mirna_context.txt'", e);
        }
    }
}
