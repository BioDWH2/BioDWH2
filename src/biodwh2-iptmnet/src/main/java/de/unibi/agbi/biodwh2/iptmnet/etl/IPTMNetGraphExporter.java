package de.unibi.agbi.biodwh2.iptmnet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.collections.Tuple2;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.iptmnet.IPTMNetDataSource;
import de.unibi.agbi.biodwh2.iptmnet.model.PTM;
import de.unibi.agbi.biodwh2.iptmnet.model.Protein;
import de.unibi.agbi.biodwh2.iptmnet.model.Score;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class IPTMNetGraphExporter extends GraphExporter<IPTMNetDataSource> {
    private static final Pattern TAXONOMY_ID_PATTERN = Pattern.compile("OX=([0-9]+)");
    public static final String PROTEIN_LABEL = "Protein";
    public static final String PTM_LABEL = "PTM";
    public static final String ORGANISM_LABEL = "Organism";
    private final Map<String, Long> OrganismMapping = new HashMap<>();
    private final Map<String, Map<String, Map<String, Long>>> PTMMapping = new HashMap<>();
    private final Map<String, Map<String, Map<String, Set<String>>>> AddedEnzymeRelations = new HashMap<>();

    public IPTMNetGraphExporter(final IPTMNetDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        OrganismMapping.clear();
        PTMMapping.clear();
        AddedEnzymeRelations.clear();
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "uniprot_accession", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "uniprot_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, "ncbi_taxid", IndexDescription.Type.UNIQUE));
        collectOrganisms(workspace, graph);
        try {
            FileUtils.openTsv(workspace, dataSource, IPTMNetUpdater.PROTEIN_FILE_NAME, Protein.class,
                              (protein) -> exportProtein(graph, protein));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + IPTMNetUpdater.PROTEIN_FILE_NAME + "'", e);
        }
        final Map<String, Map<String, Map<String, Integer>>> ptmScoreMap = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, IPTMNetUpdater.SCORE_FILE_NAME, Score.class, (score) -> {
                ptmScoreMap.computeIfAbsent(score.substrateUniProtAccession, (k) -> new HashMap<>()).computeIfAbsent(
                        score.ptmType.toLowerCase(Locale.ROOT), (k) -> new HashMap<>()).put(score.site, score.score);
            });
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + IPTMNetUpdater.SCORE_FILE_NAME + "'", e);
        }
        try {
            FileUtils.openTsv(workspace, dataSource, IPTMNetUpdater.PTM_FILE_NAME, PTM.class,
                              (ptm) -> exportPTM(graph, ptm, ptmScoreMap));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + IPTMNetUpdater.PTM_FILE_NAME + "'", e);
        }
        return true;
    }

    private void collectOrganisms(final Workspace workspace, final Graph graph) {
        final Map<String, Tuple2<Integer, Set<String>>> organismDetailsMap = new HashMap<>();
        try {
            FileUtils.openTsv(workspace, dataSource, IPTMNetUpdater.PROTEIN_FILE_NAME, Protein.class,
                              (protein) -> collectOrganism(organismDetailsMap, protein.organism));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + IPTMNetUpdater.PROTEIN_FILE_NAME + "'", e);
        }
        try {
            FileUtils.openTsv(workspace, dataSource, IPTMNetUpdater.PTM_FILE_NAME, PTM.class,
                              (ptm) -> collectOrganism(organismDetailsMap, ptm.organism));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + IPTMNetUpdater.PTM_FILE_NAME + "'", e);
        }
        for (final var entry : organismDetailsMap.entrySet()) {
            final Node node;
            if (entry.getValue().getFirst() != null) {
                node = graph.addNode(ORGANISM_LABEL, "ncbi_taxid", entry.getValue().getFirst(), "names",
                                     entry.getValue().getSecond().toArray(new String[0]));
            } else {
                node = graph.addNode(ORGANISM_LABEL, "names", entry.getValue().getSecond().toArray(new String[0]));
            }
            for (final var name : entry.getValue().getSecond())
                OrganismMapping.put(name, node.getId());
        }
    }

    private void collectOrganism(final Map<String, Tuple2<Integer, Set<String>>> organismDetailsMap,
                                 final String organism) {
        if (organism == null)
            return;
        var organismWithoutBraces = organism.replaceAll("\\s*\\(.*?\\)\\s*", "").strip();
        Integer ncbiTaxId = null;
        final var matcher = TAXONOMY_ID_PATTERN.matcher(organism);
        if (matcher.find()) {
            ncbiTaxId = Integer.parseInt(matcher.group(1));
            organismWithoutBraces = organismWithoutBraces.replace(matcher.group(), "").strip();
        }
        var entry = organismDetailsMap.get(organismWithoutBraces);
        if (entry == null) {
            if (ncbiTaxId == null) {
                final var speciesMatch = SpeciesLookup.getByScientificName(organismWithoutBraces);
                if (speciesMatch != null)
                    ncbiTaxId = speciesMatch.ncbiTaxId;
            }
            entry = new Tuple2<>(ncbiTaxId, new HashSet<>());
            organismDetailsMap.put(organismWithoutBraces, entry);
        }
        entry.getSecond().add(matcher.find() ? organism.replace(matcher.group(), "").strip() : organism);
    }

    private void exportProtein(final Graph graph, final Protein protein) {
        final Node proteinNode = graph.addNodeFromModel(protein);
        final Long organismNodeId = protein.organism != null ? OrganismMapping.get(protein.organism) : null;
        if (organismNodeId != null)
            graph.addEdge(proteinNode, organismNodeId, "BELONGS_TO");
    }

    private void exportPTM(final Graph graph, final PTM ptm,
                           Map<String, Map<String, Map<String, Integer>>> ptmScoreMap) {
        final var proteinAccession = ptm.substrateUniProtAccession;
        Integer score = null;
        final var typeScoreMap = ptmScoreMap.get(proteinAccession);
        if (typeScoreMap != null) {
            final var siteScoreMap = typeScoreMap.get(ptm.type.toLowerCase(Locale.ROOT));
            if (siteScoreMap != null)
                score = siteScoreMap.get(ptm.site);
        }
        final Long organismNodeId = ptm.organism != null ? OrganismMapping.get(ptm.organism) : null;
        var proteinNode = graph.findNode(PROTEIN_LABEL, "uniprot_accession", proteinAccession);
        if (proteinNode == null) {
            proteinNode = graph.addNode(PROTEIN_LABEL, "uniprot_accession", proteinAccession, "gene_name",
                                        ptm.substrateGeneName);
            if (organismNodeId != null)
                graph.addEdge(proteinNode, organismNodeId, "BELONGS_TO");
        }
        final String ptmType = ptm.type.toLowerCase(Locale.ROOT);
        final var siteNodeIdMap = PTMMapping.computeIfAbsent(proteinAccession, (k) -> new HashMap<>()).computeIfAbsent(
                ptmType, (k) -> new HashMap<>());
        Long nodeId = siteNodeIdMap.get(ptm.site);
        if (nodeId == null) {
            final var builder = graph.buildNode().withLabel(PTM_LABEL);
            builder.withPropertyIfNotNull("residue", ptm.site.substring(0, 1));
            String position = ptm.site.substring(1).strip();
            if (position.contains("or")) {
                String[] parts = StringUtils.split(position.replace("(", ""), "or");
                position = parts[0].strip();
                final String alternativeSite = parts[0].strip();
                builder.withPropertyIfNotNull("or_residue", alternativeSite.substring(0, 1));
                builder.withPropertyIfNotNull("or_position", Integer.parseInt(alternativeSite.substring(1)));
            }
            builder.withPropertyIfNotNull("position", Integer.parseInt(position));
            builder.withPropertyIfNotNull("type", ptmType);
            builder.withPropertyIfNotNull("score", score);
            nodeId = builder.build().getId();
            siteNodeIdMap.put(ptm.site, nodeId);
        }
        if (ptm.enzymeUniProtAccession != null) {
            Node enzymeNode = graph.findNode(PROTEIN_LABEL, "uniprot_accession", ptm.enzymeUniProtAccession);
            if (enzymeNode == null) {
                enzymeNode = graph.addNode(PROTEIN_LABEL, "uniprot_accession", ptm.enzymeUniProtAccession, "gene_name",
                                           ptm.enzymeGeneName);
                if (organismNodeId != null)
                    graph.addEdge(enzymeNode, organismNodeId, "BELONGS_TO");
            }
            final var enzymesSet = AddedEnzymeRelations.computeIfAbsent(proteinAccession, (k) -> new HashMap<>())
                                                       .computeIfAbsent(ptmType, (k) -> new HashMap<>())
                                                       .computeIfAbsent(ptm.site, (k) -> new HashSet<>());
            if (!enzymesSet.contains(ptm.enzymeUniProtAccession)) {
                graph.addEdge(nodeId, enzymeNode, "INVOLVES");
                enzymesSet.add(ptm.enzymeUniProtAccession);
            }
        }
        final var edgeBuilder = graph.buildEdge().withLabel("HAS_PTM").fromNode(proteinNode).toNode(nodeId);
        edgeBuilder.withPropertyIfNotNull("source", ptm.source);
        edgeBuilder.withPropertyIfNotNull("note", ptm.note);
        if (StringUtils.isNotEmpty(ptm.pmids))
            edgeBuilder.withProperty("pmids", Arrays.stream(StringUtils.split(ptm.pmids, ",")).map(Integer::parseInt)
                                                    .toArray(Integer[]::new));
        edgeBuilder.build();
    }
}
