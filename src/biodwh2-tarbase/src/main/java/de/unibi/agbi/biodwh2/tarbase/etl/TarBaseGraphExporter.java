package de.unibi.agbi.biodwh2.tarbase.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.EdgeBuilder;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.tarbase.TarBaseDataSource;
import de.unibi.agbi.biodwh2.tarbase.model.Entry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TarBaseGraphExporter extends GraphExporter<TarBaseDataSource> {
    static final String GENE_LABEL = "Gene";
    static final String MIRNA_LABEL = "miRNA";

    public TarBaseGraphExporter(final TarBaseDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(GENE_LABEL, "id", false, IndexDescription.Type.NON_UNIQUE));
        graph.addIndex(IndexDescription.forNode(MIRNA_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        try (TarArchiveInputStream stream = FileUtils.openTarGzip(workspace, dataSource, TarBaseUpdater.FILE_NAME)) {
            while (stream.getNextTarEntry() != null)
                exportEntries(graph, FileUtils.openSeparatedValuesFile(stream, Entry.class, '\t', true, false));
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + TarBaseUpdater.FILE_NAME + "'", e);
        }
        return true;
    }

    private void exportEntries(final Graph graph, final MappingIterator<Entry> entries) {
        final Map<String, Long> geneKeyNodeIdMap = new HashMap<>();
        final Map<String, Long> rnaNodeIdMap = new HashMap<>();
        graph.beginEdgeIndicesDelay("TARGETS");
        while (entries.hasNext()) {
            final Entry entry = entries.next();
            Long geneNodeId = getOrCreateGeneNode(graph, geneKeyNodeIdMap, entry);
            Long rnaNodeId = getOrCreateRNANode(graph, rnaNodeIdMap, entry);
            final EdgeBuilder builder = graph.buildEdge().withLabel("TARGETS").fromNode(rnaNodeId).toNode(geneNodeId);
            builder.withPropertyIfNotNull("cell_line", !"NA".equals(entry.cellLine) ? entry.cellLine : null);
            builder.withPropertyIfNotNull("tissue", !"NA".equals(entry.tissue) ? entry.tissue : null);
            builder.withPropertyIfNotNull("category", !"NA".equals(entry.category) ? entry.category : null);
            builder.withPropertyIfNotNull("method", !"NA".equals(entry.method) ? entry.method : null);
            builder.withPropertyIfNotNull("positive_negative",
                                          !"NA".equals(entry.positiveNegative) ? entry.positiveNegative : null);
            builder.withPropertyIfNotNull("direct_indirect",
                                          !"NA".equals(entry.directIndirect) ? entry.directIndirect : null);
            builder.withPropertyIfNotNull("up_down", !"NA".equals(entry.upDown) ? entry.upDown : null);
            builder.withPropertyIfNotNull("condition", !"NA".equals(entry.condition) ? entry.condition : null);
            builder.build();
        }
        graph.endEdgeIndicesDelay("TARGETS");
    }

    private Long getOrCreateGeneNode(final Graph graph, final Map<String, Long> geneKeyNodeIdMap, final Entry entry) {
        Long geneNodeId = geneKeyNodeIdMap.get(entry.geneId + "|" + entry.species);
        if (geneNodeId == null) {
            final String geneId = stripSpecies(entry.geneId);
            final String geneName = stripSpecies(entry.geneName);
            geneNodeId = graph.addNode(GENE_LABEL, "id", geneId, "name", geneName, "species", entry.species).getId();
            geneKeyNodeIdMap.put(entry.geneId + "|" + entry.species, geneNodeId);
        }
        return geneNodeId;
    }

    private String stripSpecies(final String value) {
        final int abbreviationIndex = value.indexOf('(');
        if (abbreviationIndex == -1)
            return value;
        final String bracedValue = value.substring(abbreviationIndex);
        if (bracedValue.contains(" of "))
            return value;
        return value.substring(0, abbreviationIndex).trim();
    }

    private Long getOrCreateRNANode(final Graph graph, final Map<String, Long> rnaNodeIdMap, final Entry entry) {
        Long rnaNodeId = rnaNodeIdMap.get(entry.mirna);
        if (rnaNodeId == null) {
            rnaNodeId = graph.addNode(MIRNA_LABEL, "id", entry.mirna, "species", entry.species).getId();
            rnaNodeIdMap.put(entry.mirna, rnaNodeId);
        }
        return rnaNodeId;
    }
}
