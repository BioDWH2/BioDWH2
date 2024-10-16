package de.unibi.agbi.biodwh2.qptm.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.qptm.QPTMDataSource;
import de.unibi.agbi.biodwh2.qptm.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QPTMGraphExporter extends GraphExporter<QPTMDataSource> {
    public static final String PROTEIN_LABEL = "Protein";
    public static final String PTM_LABEL = "PTM";
    public static final String ORGANISM_LABEL = "Organism";
    private static final Map<String, Integer> SPECIES_NCBI_TAX_ID_MAP = Map.of("Human", 9606, "Mouse", 10090, "Rat",
                                                                               10116, "Yeast", 4932);
    private final Map<String, Long> SpeciesMapping = new HashMap<>();
    private final Map<String, Long> ProteinMapping = new HashMap<>();
    private final Map<String, Map<String, Map<String, Map<Integer, Long>>>> PTMMapping = new HashMap<>();

    public QPTMGraphExporter(final QPTMDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        SpeciesMapping.clear();
        ProteinMapping.clear();
        PTMMapping.clear();
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "uniprot_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, "ncbi_taxid", IndexDescription.Type.UNIQUE));
        graph.beginEdgeIndicesDelay("HAS_PTM");
        graph.beginEdgeIndicesDelay("BELONGS_TO");
        try {
            FileUtils.forEachZipEntry(workspace, dataSource, QPTMUpdater.FILE_NAME, ".txt",
                                      (stream, entry) -> exportEntries(stream, graph));
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to export '" + QPTMUpdater.FILE_NAME + "'", e);
        }
        graph.endEdgeIndicesDelay("HAS_PTM");
        graph.endEdgeIndicesDelay("BELONGS_TO");
        return true;
    }

    private void exportEntries(final InputStream stream, final Graph graph) throws IOException {
        FileUtils.openTsvWithHeader(stream, Entry.class, (entry) -> exportEntry(graph, entry));
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        final var ncbiTaxId = SPECIES_NCBI_TAX_ID_MAP.get(entry.organism);
        if (!speciesFilter.isSpeciesAllowed(ncbiTaxId))
            return;
        var proteinNodeId = ProteinMapping.get(entry.uniProtAccession);
        if (proteinNodeId == null) {
            if (StringUtils.isNotEmpty(entry.geneName)) {
                proteinNodeId = graph.addNode(PROTEIN_LABEL, "uniprot_id", entry.uniProtAccession, "gene_name",
                                              entry.geneName).getId();
            } else {
                proteinNodeId = graph.addNode(PROTEIN_LABEL, "uniprot_id", entry.uniProtAccession).getId();
            }
            final var organismNodeId = getOrCreateOrganism(graph, entry.organism);
            graph.addEdge(proteinNodeId, organismNodeId, "BELONGS_TO");
            ProteinMapping.put(entry.uniProtAccession, proteinNodeId);
        }
        final var residue = String.valueOf(entry.sequenceWindow.charAt(entry.sequenceWindow.length() / 2));
        final var positionNodeIdMap = PTMMapping.computeIfAbsent(entry.uniProtAccession, (k) -> new HashMap<>())
                                                .computeIfAbsent(entry.ptm, (k) -> new HashMap<>()).computeIfAbsent(
                        residue, (k) -> new HashMap<>());
        Long ptmNodeId = positionNodeIdMap.get(entry.position);
        if (ptmNodeId == null) {
            final var ptmBuilder = graph.buildNode().withLabel(PTM_LABEL);
            ptmBuilder.withPropertyIfNotNull("position", entry.position);
            ptmBuilder.withPropertyIfNotNull("type", entry.ptm.toLowerCase(Locale.ROOT));
            ptmBuilder.withPropertyIfNotNull("sequence_window", entry.sequenceWindow);
            ptmBuilder.withPropertyIfNotNull("residue", residue);
            ptmNodeId = ptmBuilder.build().getId();
            positionNodeIdMap.put(entry.position, ptmNodeId);
        }
        graph.buildEdge("HAS_PTM").fromNode(proteinNodeId).toNode(ptmNodeId).withModel(entry).build();
    }

    private Long getOrCreateOrganism(final Graph graph, final String organism) {
        Long speciesNodeId = SpeciesMapping.get(organism);
        if (speciesNodeId == null) {
            final var ncbiTaxId = SPECIES_NCBI_TAX_ID_MAP.get(organism);
            if (ncbiTaxId != null)
                speciesNodeId = graph.addNode(ORGANISM_LABEL, "ncbi_taxid", ncbiTaxId, "name", organism).getId();
            else
                speciesNodeId = graph.addNode(ORGANISM_LABEL, "name", organism).getId();
            SpeciesMapping.put(organism, speciesNodeId);
        }
        return speciesNodeId;
    }
}
