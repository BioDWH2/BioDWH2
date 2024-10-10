package de.unibi.agbi.biodwh2.qptm.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.qptm.QPTMDataSource;
import de.unibi.agbi.biodwh2.qptm.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QPTMGraphExporter extends GraphExporter<QPTMDataSource> {
    public static final String PROTEIN_LABEL = "Protein";
    public static final String PTM_LABEL = "PTM";
    public static final String ORGANISM_LABEL = "Organism";
    private final Map<String, Long> SpeciesMapping = new HashMap<>();
    private final Map<String, Long> ProteinMapping = new HashMap<>();

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
        graph.addIndex(IndexDescription.forNode(PROTEIN_LABEL, "uniprot_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ORGANISM_LABEL, "ncbi_taxid", IndexDescription.Type.UNIQUE));
        graph.beginEdgeIndicesDelay("HAS_PTM");
        graph.beginEdgeIndicesDelay("HAS_SPECIES");
        try {
            FileUtils.forEachZipEntry(workspace, dataSource, QPTMUpdater.FILE_NAME, ".txt",
                                      (stream, entry) -> exportEntries(stream, graph));
        } catch (Exception e) {
            throw new ExporterFormatException("Failed to export '" + QPTMUpdater.FILE_NAME + "'", e);
        }
        graph.endEdgeIndicesDelay("HAS_PTM");
        graph.endEdgeIndicesDelay("HAS_SPECIES");
        return true;
    }

    private void exportEntries(final InputStream stream, final Graph graph) throws IOException {
        FileUtils.openTsvWithHeader(stream, Entry.class, (entry) -> exportEntry(graph, entry));
    }

    private void exportEntry(final Graph graph, final Entry entry) {
        var proteinNodeId = ProteinMapping.get(entry.uniProtAccession);
        if (proteinNodeId == null) {
            if (StringUtils.isNotEmpty(entry.geneName)) {
                proteinNodeId = graph.addNode(PROTEIN_LABEL, "uniprot_id", entry.uniProtAccession, "gene_name",
                                              entry.geneName).getId();
            } else {
                proteinNodeId = graph.addNode(PROTEIN_LABEL, "uniprot_id", entry.uniProtAccession).getId();
            }
            ProteinMapping.put(entry.uniProtAccession, proteinNodeId);
        }
        final var ptmNode = graph.addNodeFromModel(entry);
        graph.addEdge(proteinNodeId, ptmNode, "HAS_PTM");
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
