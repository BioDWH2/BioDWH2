package de.unibi.agbi.biodwh2.ptmd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.ptmd.PTMDDataSource;
import de.unibi.agbi.biodwh2.ptmd.model.Entry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class PTMDGraphExporter extends GraphExporter<PTMDDataSource> {
    public static final String PROTEIN_LABEL = "Protein";
    public static final String PTM_LABEL = "PTM";
    public static final String DISEASE_LABEL = "Disease";
    private final Map<String, Long> PTMMapping = new HashMap<>();
    private final Map<String, Long> ProteinMapping = new HashMap<>();
    private final Map<String, Long> DiseaseMapping = new HashMap<>();
    private final Map<String, Set<String>> AddedEnzymeRelations = new HashMap<>();
    private static final Map<String, String> REGULATION_TYPES = Map.of("N", "disruption", "C", "creation", "A",
                                                                       "absence", "U", "up-regulation", "D",
                                                                       "down-regulation", "P", "presence");
    private static final Set<String> AMINO_ACID_NAMES = Set.of("alanine", "arginine", "asparagine", "aspartic acid",
                                                               "cysteine", "glutamic acid", "glutamine", "glycine",
                                                               "histidine", "isoleucine", "leucine", "lysine",
                                                               "methionine", "phenylalanine", "proline", "serine",
                                                               "threonine", "tryptophan", "tyrosine", "valine");

    public PTMDGraphExporter(final PTMDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 2;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        PTMMapping.clear();
        ProteinMapping.clear();
        DiseaseMapping.clear();
        AddedEnzymeRelations.clear();
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "uniprot_accession", IndexDescription.Type.UNIQUE));
        exportEntries(workspace, graph);
        return true;
    }

    private void exportEntries(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.forEachZipEntry(workspace, dataSource, PTMDUpdater.TOTAL_FILE_NAME, "Total.txt",
                                      ((stream, entry) -> FileUtils.openTsvWithHeader(stream, Entry.class,
                                                                                      (e) -> exportEntry(graph, e))));
        } catch (Exception e) {
            throw new ExporterException("Failed to export '" + PTMDUpdater.TOTAL_FILE_NAME + "'", e);
        }
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        if (StringUtils.isEmpty(entry.position) || StringUtils.isEmpty(entry.residue))
            return;
        String ptmType = entry.type.toLowerCase(Locale.ROOT);
        for (var aminoAcidName : AMINO_ACID_NAMES)
            ptmType = ptmType.replace(aminoAcidName, "").strip();
        final var ptmPosition = Integer.parseInt(
                entry.position.contains(".") ? StringUtils.split(entry.position, ".", 2)[0] : entry.position);
        final String ptmKey = entry.uniProt + "_" + ptmType + "_" + ptmPosition + "_" + entry.residue;
        Long ptmNodeId = PTMMapping.get(ptmKey);
        if (ptmNodeId == null) {
            final Long proteinNodeId = getOrCreateProtein(graph, entry.uniProt, entry.geneName);
            ptmNodeId = graph.addNode(PTM_LABEL, "type", ptmType, "position", ptmPosition, "residue", entry.residue)
                             .getId();
            PTMMapping.put(ptmKey, ptmNodeId);
            graph.addEdge(proteinNodeId, ptmNodeId, "HAS_PTM");
        }
        if (entry.enzyme != null) {
            final var enzymeAccessions = StringUtils.split(entry.enzyme, "/");
            final var enzymesSet = AddedEnzymeRelations.computeIfAbsent(ptmKey, (k) -> new HashSet<>());
            for (final var enzymeAccession : enzymeAccessions) {
                final Long enzymeNodeId = getOrCreateProtein(graph, enzymeAccession, null);
                if (!enzymesSet.contains(enzymeAccession)) {
                    graph.addEdge(ptmNodeId, enzymeNodeId, "INVOLVES");
                    enzymesSet.add(enzymeAccession);
                }
            }
        }
        final Long diseaseNodeId = getOrCreateDisease(graph, entry.disease);
        final var mutationSites = convertPythonArray(entry.mutationSite);
        final String[] mutationSiteImpacts = new String[mutationSites.length];
        for (int i = 0; i < mutationSites.length; i++) {
            var mutationSite = mutationSites[i].trim();
            var mutationSitePosition = mutationSite.substring(1, mutationSite.length() - 1);
            if (NumberUtils.isDigits(mutationSitePosition)) {
                var distance = Math.abs(ptmPosition - Integer.parseInt(mutationSitePosition));
                if (distance == 0)
                    mutationSiteImpacts[i] = "direct";
                else if (distance < 3)
                    mutationSiteImpacts[i] = "proximal";
                else
                    mutationSiteImpacts[i] = "distal";
            } else {
                mutationSiteImpacts[i] = "unknown";
            }
        }
        final var edgeBuilder = graph.buildEdge().withLabel("ASSOCIATED_WITH").fromNode(ptmNodeId).toNode(
                diseaseNodeId);
        edgeBuilder.withPropertyIfNotNull(ID_KEY, entry.pdasId);
        edgeBuilder.withPropertyIfNotNull("regulation", REGULATION_TYPES.get(entry.state));
        edgeBuilder.withPropertyIfNotNull("sources", convertPythonArray(entry.source));
        edgeBuilder.withPropertyIfNotNull("mutation_sites", mutationSites);
        edgeBuilder.withPropertyIfNotNull("mutation_site_impacts", mutationSiteImpacts);
        edgeBuilder.withPropertyIfNotNull("sentence", entry.sentence);
        edgeBuilder.withPropertyIfNotNull("cell_type", entry.cellType);
        edgeBuilder.withPropertyIfNotNull("is_experimental_verification", entry.isExperimentalVerification == 1);
        if (StringUtils.isNotEmpty(entry.pmids))
            edgeBuilder.withProperty("pmids", Arrays.stream(StringUtils.split(entry.pmids, ";")).map(Integer::parseInt)
                                                    .toArray(Integer[]::new));
        edgeBuilder.build();
    }

    private Long getOrCreateProtein(final Graph graph, final String uniProt, final String geneName) {
        Long nodeId = ProteinMapping.get(uniProt);
        if (nodeId == null) {
            if (StringUtils.isNotEmpty(geneName)) {
                final String[] geneNames = StringUtils.splitByWholeSeparator(geneName, "; ");
                nodeId = graph.addNode(PROTEIN_LABEL, "uniprot_accession", uniProt, "gene_names", geneNames).getId();
            } else {
                nodeId = graph.addNode(PROTEIN_LABEL, "uniprot_accession", uniProt).getId();
            }
            ProteinMapping.put(uniProt, nodeId);
        }
        return nodeId;
    }

    private Long getOrCreateDisease(final Graph graph, final String name) {
        Long nodeId = DiseaseMapping.get(name);
        if (nodeId == null) {
            nodeId = graph.addNode(DISEASE_LABEL, "name", name).getId();
            DiseaseMapping.put(name, nodeId);
        }
        return nodeId;
    }

    private String[] convertPythonArray(String text) {
        if (StringUtils.isEmpty(text))
            return new String[0];
        text = StringUtils.stripStart(text, "[");
        text = StringUtils.stripEnd(text, "]");
        final String[] array = StringUtils.splitByWholeSeparator(text, ", ");
        for (var i = 0; i < array.length; i++)
            array[i] = StringUtils.strip(array[i], "'");
        return array;
    }
}
