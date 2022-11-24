package de.unibi.agbi.biodwh2.mirdb.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.mapping.SpeciesLookup;
import de.unibi.agbi.biodwh2.core.model.Configuration;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.mirdb.MiRDBDataSource;
import de.unibi.agbi.biodwh2.mirdb.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MiRDBGraphExporter extends GraphExporter<MiRDBDataSource> {
    static final String MIRNA_LABEL = "miRNA";
    static final String GENE_LABEL = "Gene";
    static final String TARGETS_LABEL = "TARGETS";
    static final String GENBANK_ACCESSION_KEY = "genbank_accession";

    private final Map<String, SpeciesLookup.Entry> speciesMap = new HashMap<>();

    public MiRDBGraphExporter(final MiRDBDataSource dataSource) {
        super(dataSource);
        speciesMap.put("cfa", SpeciesLookup.CANIS_FAMILIARIS);
        speciesMap.put("gga", SpeciesLookup.GALLUS_GALLUS);
        speciesMap.put("hsa", SpeciesLookup.HOMO_SAPIENS);
        speciesMap.put("mmu", SpeciesLookup.MUS_MUSCULUS);
        speciesMap.put("rno", SpeciesLookup.RATTUS_NORVEGICUS);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(MIRNA_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, GENBANK_ACCESSION_KEY, IndexDescription.Type.UNIQUE));
        final Configuration.GlobalProperties.SpeciesFilter speciesFilter = workspace.getConfiguration()
                                                                                    .getGlobalProperties()
                                                                                    .getSpeciesFilter();
        final double scoreThreshold = getScoreThreshold(workspace);
        graph.beginEdgeIndicesDelay(TARGETS_LABEL);
        exportEntries(workspace, graph, speciesFilter, scoreThreshold);
        graph.endEdgeIndicesDelay(TARGETS_LABEL);
        return true;
    }

    private double getScoreThreshold(final Workspace workspace) {
        final String scoreThreshold = dataSource.getProperties(workspace).get("scoreThreshold");
        if (scoreThreshold != null) {
            try {
                return Double.parseDouble(scoreThreshold);
            } catch (Exception ignored) {
            }
        }
        return 50.0;
    }

    private void exportEntries(final Workspace workspace, final Graph graph,
                               final Configuration.GlobalProperties.SpeciesFilter speciesFilter,
                               final double scoreThreshold) {
        try (final MappingIterator<Entry> entries = FileUtils.openGzipTsv(workspace, dataSource, MiRDBUpdater.FILE_NAME,
                                                                          Entry.class)) {
            while (entries.hasNext())
                exportEntry(graph, entries.next(), speciesFilter, scoreThreshold);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportEntry(final Graph graph, final Entry entry,
                             final Configuration.GlobalProperties.SpeciesFilter speciesFilter,
                             final double scoreThreshold) {
        if (entry.targetScore != null && entry.targetScore < scoreThreshold)
            return;
        Node mirnaNode = graph.findNode(MIRNA_LABEL, ID_KEY, entry.mirnaId);
        if (mirnaNode == null) {
            final String prefix = StringUtils.split(entry.mirnaId, "-", 2)[1];
            final SpeciesLookup.Entry speciesEntry = speciesMap.get(prefix);
            if (speciesFilter.isSpeciesAllowed(speciesEntry != null ? speciesEntry.ncbiTaxId : null)) {
                if (speciesEntry != null) {
                    mirnaNode = graph.addNode(MIRNA_LABEL, ID_KEY, entry.mirnaId, "species_ncbi_taxid",
                                              speciesEntry.ncbiTaxId);
                } else {
                    mirnaNode = graph.addNode(MIRNA_LABEL, ID_KEY, entry.mirnaId);
                }
            }
        }
        if (mirnaNode != null) {
            Node geneNode = graph.findNode(GENE_LABEL, GENBANK_ACCESSION_KEY, entry.genBankAccession);
            if (geneNode == null)
                geneNode = graph.addNode(GENE_LABEL, GENBANK_ACCESSION_KEY, entry.genBankAccession);
            graph.addEdge(mirnaNode, geneNode, TARGETS_LABEL, "score", entry.targetScore);
        }
    }
}
