package de.unibi.agbi.biodwh2.rnalocate.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.rnalocate.RNALocateDataSource;
import de.unibi.agbi.biodwh2.rnalocate.model.ExperimentalEntry;
import de.unibi.agbi.biodwh2.rnalocate.model.PredictedEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

public class RNALocateGraphExporter extends GraphExporter<RNALocateDataSource> {
    private static final String LOCALIZATION_LABEL = "Localization";
    static final String RNA_LABEL = "RNA";

    private final Map<String, Long> localizationNodeIdMap = new HashMap<>();
    private final Map<String, Long> rnaNodeIdMap = new HashMap<>();

    public RNALocateGraphExporter(final RNALocateDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 3;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        localizationNodeIdMap.clear();
        rnaNodeIdMap.clear();
        exportExperimentalEntries(workspace, graph);
        exportPredictedEntries(workspace, graph, RNALocateUpdater.PREDICTED_MRNA_FILE_NAME);
        exportPredictedEntries(workspace, graph, RNALocateUpdater.PREDICTED_LNCRNA_FILE_NAME);
        exportPredictedEntries(workspace, graph, RNALocateUpdater.PREDICTED_MIRNA_FILE_NAME);
        exportPredictedEntries(workspace, graph, RNALocateUpdater.PREDICTED_SNORNA_FILE_NAME);
        return true;
    }

    private void exportExperimentalEntries(final Workspace workspace, final Graph graph) {
        try (final ZipInputStream inputStream = FileUtils.openZip(workspace, dataSource,
                                                                  RNALocateUpdater.EXPERIMENTAL_FILE_NAME)) {
            inputStream.getNextEntry();
            final var entries = FileUtils.openTsvWithHeader(inputStream, ExperimentalEntry.class);
            while (entries.hasNext())
                exportExperimentalEntry(graph, entries.next());
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + RNALocateUpdater.EXPERIMENTAL_FILE_NAME + "'", e);
        }
    }

    private void exportExperimentalEntry(final Graph graph, final ExperimentalEntry entry) {
        final var rnaNodeId = getOrCreateRNANode(graph, entry.rnaSymbol, entry.rnaType);
        final var localizationNodeId = getOrCreateLocalizationNode(graph, entry.subcellularLocalization,
                                                                   entry.goAccession);
        final var builder = graph.buildEdge().fromNode(rnaNodeId).toNode(localizationNodeId).withLabel("LOCALIZED_IN");
        builder.withPropertyIfNotNull(ID_KEY, entry.id);
        builder.withPropertyIfNotNull("score", entry.score);
        builder.withPropertyIfNotNull("species", entry.species);
        builder.withPropertyIfNotNull("pmid", entry.pmid);
        builder.withPropertyIfNotNull("evidence", "experimental");
        builder.build();
    }

    private Long getOrCreateRNANode(final Graph graph, final String symbol, final String type) {
        final String key = type + "|" + symbol;
        Long nodeId = rnaNodeIdMap.get(key);
        if (nodeId == null) {
            nodeId = graph.addNode(RNA_LABEL, "symbol", symbol, "type", type).getId();
            rnaNodeIdMap.put(key, nodeId);
        }
        return nodeId;
    }

    private Long getOrCreateLocalizationNode(final Graph graph, final String localization, final String goAccession) {
        Long nodeId = localizationNodeIdMap.get(localization);
        if (nodeId == null) {
            if (goAccession != null) {
                nodeId = graph.addNode(LOCALIZATION_LABEL, ID_KEY, goAccession, "name", localization,
                                       OntologyGraphExporter.IS_PROXY_KEY, true).getId();
            } else {
                nodeId = graph.addNode(LOCALIZATION_LABEL, "name", localization).getId();
            }
            localizationNodeIdMap.put(localization, nodeId);
        }
        return nodeId;
    }

    private void exportPredictedEntries(final Workspace workspace, final Graph graph, final String fileName) {
        try (final ZipInputStream inputStream = FileUtils.openZip(workspace, dataSource, fileName)) {
            inputStream.getNextEntry();
            final var entries = FileUtils.openTsvWithHeader(inputStream, PredictedEntry.class);
            while (entries.hasNext())
                exportPredictedEntry(graph, entries.next());
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export '" + fileName + "'", e);
        }
    }

    private void exportPredictedEntry(final Graph graph, final PredictedEntry entry) {
        final var rnaNodeId = getOrCreateRNANode(graph, entry.rnaSymbol, entry.rnaType);
        final var localizationNodeId = getOrCreateLocalizationNode(graph, entry.subcellularLocalization,
                                                                   entry.goAccession);
        final var builder = graph.buildEdge().fromNode(rnaNodeId).toNode(localizationNodeId).withLabel("LOCALIZED_IN");
        builder.withPropertyIfNotNull(ID_KEY, entry.id);
        builder.withPropertyIfNotNull("score", entry.score);
        builder.withPropertyIfNotNull("species", entry.species);
        builder.withPropertyIfNotNull("algorithm", entry.algorithm);
        builder.withPropertyIfNotNull("evidence", "predicted");
        builder.build();
    }
}
