package de.unibi.agbi.biodwh2.qptmplants.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.qptmplants.QPTMPlantsDataSource;
import de.unibi.agbi.biodwh2.qptmplants.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QPTMPlantsGraphExporter extends GraphExporter<QPTMPlantsDataSource> {
    public static final String PROTEIN_LABEL = "Protein";
    public static final String PTM_LABEL = "PTM";
    public static final String ORGANISM_LABEL = "Organism";
    private final Map<String, Long> SpeciesMapping = new HashMap<>();

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
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "uniprot_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, "ncbi_taxid", IndexDescription.Type.UNIQUE));
        graph.beginEdgeIndicesDelay("HAS_PTM");
        graph.beginEdgeIndicesDelay("HAS_SPECIES");
        try {
            FileUtils.forEachZipEntry(workspace, dataSource, QPTMPlantsUpdater.FILE_NAME, ".txt",
                                      (stream, entry) -> exportEntries(stream, graph));
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to export '" + QPTMPlantsUpdater.FILE_NAME + "'", e);
        }
        graph.endEdgeIndicesDelay("HAS_PTM");
        graph.endEdgeIndicesDelay("HAS_SPECIES");
        return true;
    }

    private void exportEntries(final InputStream stream, final Graph graph) throws IOException {
        FileUtils.openTsvWithHeader(stream, Entry.class, (entry) -> exportEntry(graph, entry));
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        var proteinNode = graph.findNode(PROTEIN_LABEL, "uniprot_id", entry.proteinId);
        if (proteinNode == null) {
            if (StringUtils.isNotEmpty(entry.geneName) && !"N/A".equalsIgnoreCase(entry.geneName)) {
                proteinNode = graph.addNode(PROTEIN_LABEL, "uniprot_id", entry.proteinId, "gene_name", entry.geneName);
            } else {
                proteinNode = graph.addNode(PROTEIN_LABEL, "uniprot_id", entry.proteinId);
            }
        }
        final var ptmNode = graph.addNodeFromModel(entry);
        graph.addEdge(proteinNode, ptmNode, "HAS_PTM");
        findOrAddOrganism(graph, entry.organism, ptmNode);
    }

    private void findOrAddOrganism(final Graph graph, final String organism, final Node ptmNode) {
        Long speciesNodeId = SpeciesMapping.get(organism);
        if (speciesNodeId == null) {
            final var species = SpeciesLookup.getByScientificName(organism);
            if (species != null && species.ncbiTaxId != null) {
                speciesNodeId = graph.addNode(ORGANISM_LABEL, "ncbi_taxid", species.ncbiTaxId, "name", organism)
                                     .getId();
            } else {
                speciesNodeId = graph.addNode(ORGANISM_LABEL, "name", organism).getId();
            }
            SpeciesMapping.put(organism, speciesNodeId);
        }
        graph.addEdge(ptmNode, speciesNodeId, "HAS_SPECIES");
    }
}
