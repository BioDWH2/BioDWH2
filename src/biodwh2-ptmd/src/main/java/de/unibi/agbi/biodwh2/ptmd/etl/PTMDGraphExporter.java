package de.unibi.agbi.biodwh2.ptmd.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeBuilder;
import de.unibi.agbi.biodwh2.ptmd.PTMDDataSource;
import de.unibi.agbi.biodwh2.ptmd.model.PTM;
import de.unibi.agbi.biodwh2.ptmd.model.PTMSite;
import de.unibi.agbi.biodwh2.ptmd.model.Protein;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;

public class PTMDGraphExporter extends GraphExporter<PTMDDataSource> {
    public static final String PROTEIN_LABEL = "Protein";
    public static final String PTM_LABEL = "PTM";
    public static final String DISEASE_LABEL = "Disease";
    public static final String SPECIES_LABEL = "Species";
    private final Map<String, Long> SpeciesMapping = new HashMap<>();
    private final Map<String, Long> DiseaseMapping = new HashMap<>();
    private final Map<String, Map<String, Long>> UniProtIdPositionPTMMap = new HashMap<>();
    private static final Map<String, String> AminoAcidMapping = Map.ofEntries(Map.entry("Alanine", "A"),
                                                                              Map.entry("Arginine", "R"),
                                                                              Map.entry("Asparagine", "N"),
                                                                              Map.entry("Aspartic acid", "D"),
                                                                              Map.entry("Cysteine", "C"),
                                                                              Map.entry("Glutamine", "Q"),
                                                                              Map.entry("Glutamic acid", "E"),
                                                                              Map.entry("Glycine", "G"),
                                                                              Map.entry("Histidine", "H"),
                                                                              Map.entry("Isoleucine", "I"),
                                                                              Map.entry("Leucine", "L"),
                                                                              Map.entry("Lysine", "K"),
                                                                              Map.entry("Methionine", "M"),
                                                                              Map.entry("Phenylalanine", "F"),
                                                                              Map.entry("Proline", "P"),
                                                                              Map.entry("Serine", "S"),
                                                                              Map.entry("Threonine", "T"),
                                                                              Map.entry("Tryptophan", "W"),
                                                                              Map.entry("Tyrosine", "Y"),
                                                                              Map.entry("Valine", "V"));


    public PTMDGraphExporter(final PTMDDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        SpeciesMapping.clear();
        DiseaseMapping.clear();
        UniProtIdPositionPTMMap.clear();
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "uniprot_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(SPECIES_LABEL, "ncbi_taxid", IndexDescription.Type.UNIQUE));
        exportProteins(workspace, graph);
        exportPTMSites(workspace, graph);
        exportPTMs(workspace, graph);
        return true;
    }

    private void exportProteins(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.forEachZipEntry(workspace, dataSource, PTMDUpdater.PROTEIN_INFORMATION_FILE_NAME,
                                      "/annotation.txt",
                                      ((stream, entry) -> FileUtils.openTsvWithHeader(stream, Protein.class,
                                                                                      graph::addNodeFromModel)));
        } catch (Exception e) {
            throw new ExporterException("Failed to export '" + PTMDUpdater.PROTEIN_INFORMATION_FILE_NAME + "'", e);
        }
    }

    private void exportPTMSites(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.forEachZipEntry(workspace, dataSource, PTMDUpdater.PTM_SITES_FILE_NAME, ".txt",
                                      (stream, entry) -> exportPTMSiteFile(stream, entry, graph));
        } catch (Exception e) {
            throw new ExporterException("Failed to export PTMs from '" + PTMDUpdater.PTM_SITES_FILE_NAME + "'", e);
        }
    }

    private void exportPTMSiteFile(final InputStream stream, final ZipEntry entry,
                                   final Graph graph) throws IOException {
        final String fileName = entry.getName().substring(entry.getName().lastIndexOf('/') + 1);
        String ptmType = fileName.substring(0, fileName.lastIndexOf('.'));
        if (ptmType.equals("phosphorylation"))
            ptmType = "Phosphorylation";
        final String type = ptmType;
        FileUtils.openTsvWithHeader(stream, PTMSite.class, ptm -> {
            final var nodeId = graph.addNodeFromModel(ptm, "type", type.toLowerCase(Locale.ROOT)).getId();
            Node proteinNode = graph.findNode(PROTEIN_LABEL, "uniprot_id", ptm.uniprotId);
            if (proteinNode == null)
                proteinNode = graph.addNode(PROTEIN_LABEL, "uniprot_id", ptm.uniprotId);
            graph.addEdge(proteinNode, nodeId, "HAS_PTM");
            if (ptm.position != null) {
                UniProtIdPositionPTMMap.computeIfAbsent(ptm.uniprotId, (id) -> new HashMap<>()).put(ptm.position,
                                                                                                    nodeId);
            }
        });
    }

    private void exportPTMs(final Workspace workspace, final Graph graph) {
        try {
            FileUtils.forEachZipEntry(workspace, dataSource, PTMDUpdater.PTM_DISEASE_ASSOCIATION_FILE_NAME, "/data.txt",
                                      (stream, entry) -> FileUtils.openTsv(stream, PTM.class,
                                                                           ptm -> createOrUpdatePTMNode(graph, ptm)));
        } catch (Exception e) {
            throw new ExporterException("Failed to export '" + PTMDUpdater.PTM_DISEASE_ASSOCIATION_FILE_NAME + "'", e);
        }
    }

    private void createOrUpdatePTMNode(final Graph graph, final PTM ptm) {
        Node ptmNode = null;
        if (ptm.position != null) {
            final var positionCandidates = UniProtIdPositionPTMMap.get(ptm.uniprotId);
            if (positionCandidates != null) {
                final Long nodeId = positionCandidates.get(ptm.position);
                if (nodeId != null) {
                    ptmNode = graph.getNode(nodeId);
                    if (ptmNode != null)
                        updatePTMNode(graph, ptm, ptmNode);
                }
            }
        }
        if (ptmNode == null) {
            ptmNode = createPTMNode(graph, ptm);
            final Node proteinNode = graph.findNode(PROTEIN_LABEL, "uniprot_id", ptm.uniprotId);
            graph.addEdge(proteinNode, ptmNode, "HAS_PTM");
        }
        if (ptm.species != null)
            findOrAddSpecies(graph, ptm, ptmNode);
        if (ptm.disease != null)
            findOrAddDisease(graph, ptm, ptmNode);
    }

    private void updatePTMNode(final Graph graph, final PTM ptm, final Node node) {
        node.setProperty("gene_name", ptm.geneName);
        node.setProperty("state", ptm.state);
        node.setProperty("residue", ptm.residue);
        String ptmTypeNode = node.getProperty("type");
        String[] ptmTypeNewWords = ptm.ptmType.split("\\s+");
        if (ptmTypeNewWords.length > 1) {
            String ptmResidue = ptm.residue;
            String ptmTypeAminoAcid = AminoAcidMapping.get(ptmTypeNewWords[0]);
            if (!ptmTypeAminoAcid.equals(ptmResidue))
                node.setProperty("residue", ptmTypeAminoAcid);
            if (ptmTypeNode.contains(ptmTypeNewWords[1].toLowerCase(Locale.ROOT)))
                node.setProperty("type", ptm.ptmType.toLowerCase(Locale.ROOT));
        }
        graph.update(node);
    }

    private Node createPTMNode(final Graph graph, final PTM ptm) {
        final NodeBuilder builder = graph.buildNode().withLabel(PTM_LABEL);
        builder.withPropertyIfNotNull("gene_name", ptm.geneName);
        builder.withPropertyIfNotNull("type", ptm.ptmType.toLowerCase(Locale.ROOT));
        builder.withPropertyIfNotNull("state", ptm.state);
        builder.withPropertyIfNotNull("position", ptm.position);
        final String[] ptmTypeNewWords = ptm.ptmType.split("\\s+");
        if (ptmTypeNewWords.length > 1) {
            final String ptmTypeAminoAcid = AminoAcidMapping.get(ptmTypeNewWords[0]);
            builder.withPropertyIfNotNull("residue", ptm.residue != null ? ptm.residue : ptmTypeAminoAcid);
        }
        return builder.build();
    }

    private void findOrAddSpecies(final Graph graph, final PTM ptm, final Node ptmNode) {
        String speciesName = ptm.species.replaceAll("\\s*\\(.*?\\)\\s*", "");
        Long speciesNodeId = SpeciesMapping.get(speciesName);
        if (speciesNodeId == null) {
            final var species = SpeciesLookup.getByScientificName(speciesName);
            if (species != null && species.ncbiTaxId != null) {
                speciesNodeId = graph.addNode(SPECIES_LABEL, "ncbi_taxid", species.ncbiTaxId, "name", speciesName)
                                     .getId();
                SpeciesMapping.put(speciesName, speciesNodeId);
            } else {
                speciesNodeId = graph.addNode(SPECIES_LABEL, "name", speciesName).getId();
                SpeciesMapping.put(speciesName, speciesNodeId);
            }
        }
        graph.addEdge(ptmNode, speciesNodeId, "HAS_SPECIES");
    }

    private void findOrAddDisease(final Graph graph, final PTM ptm, final Node ptmNode) {
        Long diseaseNodeId = DiseaseMapping.get(ptm.disease);
        if (diseaseNodeId == null) {
            diseaseNodeId = graph.addNode(DISEASE_LABEL, "names", StringUtils.split(ptm.disease, '/')).getId();
            DiseaseMapping.put(ptm.disease, diseaseNodeId);
        }
        graph.addEdge(ptmNode, diseaseNodeId, "IN_DISEASE", "literature", ptm.literature);
    }
}
