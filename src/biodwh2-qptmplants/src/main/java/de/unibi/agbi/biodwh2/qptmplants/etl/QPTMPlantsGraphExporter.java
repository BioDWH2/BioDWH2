package de.unibi.agbi.biodwh2.qptmplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.qptmplants.QPTMPlantsDataSource;
import de.unibi.agbi.biodwh2.qptmplants.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class QPTMPlantsGraphExporter extends GraphExporter<QPTMPlantsDataSource> {
    public static final String PROTEIN_LABEL = "Protein";
    public static final String PTM_LABEL = "PTM";
    public static final String ORGANISM_LABEL = "Organism";
    private final Map<String, Long> SpeciesMapping = new HashMap<>();
    private final Map<String, Long> ProteinMapping = new HashMap<>();
    private final Map<String, Map<String, Map<String, Map<Integer, Long>>>> PTMMapping = new HashMap<>();

    public QPTMPlantsGraphExporter(final QPTMPlantsDataSource dataSource) {
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
            FileUtils.forEachZipEntry(workspace, dataSource, QPTMPlantsUpdater.FILE_NAME, ".txt",
                                      (stream, entry) -> exportEntries(stream, graph));
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to export '" + QPTMPlantsUpdater.FILE_NAME + "'", e);
        }
        graph.endEdgeIndicesDelay("HAS_PTM");
        graph.endEdgeIndicesDelay("BELONGS_TO");
        return true;
    }

    private void exportEntries(final InputStream stream, final Graph graph) throws IOException {
        FileUtils.openTsvWithHeader(stream, Entry.class, (entry) -> exportEntry(graph, entry));
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        final var species = SpeciesLookup.getByScientificName(entry.organism);
        final var ncbiTaxId = species != null ? species.ncbiTaxId : null;
        if (!speciesFilter.isSpeciesAllowed(ncbiTaxId))
            return;
        var proteinNodeId = ProteinMapping.get(entry.proteinId);
        if (proteinNodeId == null) {
            if (StringUtils.isNotEmpty(entry.geneName) && !"N/A".equalsIgnoreCase(entry.geneName)) {
                proteinNodeId = graph.addNode(PROTEIN_LABEL, "uniprot_id", entry.proteinId, "gene_name", entry.geneName)
                                     .getId();
            } else {
                proteinNodeId = graph.addNode(PROTEIN_LABEL, "uniprot_id", entry.proteinId).getId();
            }
            final var organismNodeId = getOrCreateOrganism(graph, entry.organism, ncbiTaxId);
            graph.addEdge(proteinNodeId, organismNodeId, "BELONGS_TO");
            ProteinMapping.put(entry.proteinId, proteinNodeId);
        }
        final var residue = String.valueOf(entry.sequenceWindow.charAt(entry.sequenceWindow.length() / 2));
        final var positionNodeIdMap = PTMMapping.computeIfAbsent(entry.proteinId, (k) -> new HashMap<>())
                                                .computeIfAbsent(entry.modification, (k) -> new HashMap<>())
                                                .computeIfAbsent(residue, (k) -> new HashMap<>());
        Long ptmNodeId = positionNodeIdMap.get(entry.position);
        if (ptmNodeId == null) {
            final var ptmBuilder = graph.buildNode().withLabel(PTM_LABEL);
            ptmBuilder.withPropertyIfNotNull("position", entry.position);
            ptmBuilder.withPropertyIfNotNull("type", entry.modification.toLowerCase(Locale.ROOT));
            ptmBuilder.withPropertyIfNotNull("sequence_window", entry.sequenceWindow);
            ptmBuilder.withPropertyIfNotNull("residue", residue);
            ptmNodeId = ptmBuilder.build().getId();
            positionNodeIdMap.put(entry.position, ptmNodeId);
        }
        graph.buildEdge("HAS_PTM").fromNode(proteinNodeId).toNode(ptmNodeId).withModel(entry).build();
    }

    private Long getOrCreateOrganism(final Graph graph, final String organism, final Integer ncbiTaxId) {
        Long speciesNodeId = SpeciesMapping.get(organism);
        if (speciesNodeId == null) {
            if (ncbiTaxId != null)
                speciesNodeId = graph.addNode(ORGANISM_LABEL, "ncbi_taxid", ncbiTaxId, "name", organism).getId();
            else
                speciesNodeId = graph.addNode(ORGANISM_LABEL, "name", organism).getId();
            SpeciesMapping.put(organism, speciesNodeId);
        }
        return speciesNodeId;
    }
}
