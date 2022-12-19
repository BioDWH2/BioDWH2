package de.unibi.agbi.biodwh2.rnalocate.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.rnalocate.RNALocateDataSource;
import de.unibi.agbi.biodwh2.rnalocate.model.DatabaseEntry;
import de.unibi.agbi.biodwh2.rnalocate.model.ExperimentEntry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

public class RNALocateGraphExporter extends GraphExporter<RNALocateDataSource> {
    private static final String LOCALIZATION_LABEL = "Localization";
    static final String RNA_LABEL = "RNA";

    public RNALocateGraphExporter(final RNALocateDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(RNA_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
        final Map<String, Long> localizationNodeIdMap = new HashMap<>();
        exportExperimentalEntries(workspace, graph, localizationNodeIdMap);
        exportDatabaseEntries(workspace, graph, localizationNodeIdMap);
        return true;
    }

    private void exportExperimentalEntries(final Workspace workspace, final Graph graph,
                                           final Map<String, Long> localizationNodeIdMap) {
        try (final ZipInputStream inputStream = FileUtils.openZip(workspace, dataSource,
                                                                  RNALocateUpdater.EXPERIMENTAL_FILE_NAME)) {
            inputStream.getNextEntry();
            final MappingIterator<ExperimentEntry> entries = FileUtils.openSeparatedValuesFile(inputStream,
                                                                                               ExperimentEntry.class,
                                                                                               '\t', true);
            while (entries.hasNext())
                exportExperimentalEntry(graph, entries.next(), localizationNodeIdMap);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportExperimentalEntry(final Graph graph, final ExperimentEntry entry,
                                         final Map<String, Long> localizationNodeIdMap) {
        final Long localizationNodeId = getOrCreateLocalizationNode(graph, localizationNodeIdMap,
                                                                    entry.subCellularLocalization);
        final Node rnaNode = getOrCreateRNANode(graph, entry.geneId, entry.geneName, entry.geneSymbol,
                                                entry.rnaCategory);
        graph.addEdge(rnaNode, localizationNodeId, "LOCALIZED_IN", ID_KEY, entry.id, "description", entry.description,
                      "species", entry.species, "pmid", entry.pmid);
    }

    private Long getOrCreateLocalizationNode(final Graph graph, final Map<String, Long> localizationNodeIdMap,
                                             final String localization) {
        Long nodeId = localizationNodeIdMap.get(localization);
        if (nodeId == null) {
            nodeId = graph.addNode(LOCALIZATION_LABEL, "name", localization).getId();
            localizationNodeIdMap.put(localization, nodeId);
        }
        return nodeId;
    }

    private Node getOrCreateRNANode(final Graph graph, final String id, final String name, final String symbol,
                                    final String category) {
        Node node = graph.findNode(RNA_LABEL, ID_KEY, id);
        if (node == null) {
            if (name != null)
                node = graph.addNode(RNA_LABEL, ID_KEY, id, "symbol", symbol, "category", category, "name", name);
            else
                node = graph.addNode(RNA_LABEL, ID_KEY, id, "symbol", symbol, "category", category);
        }
        return node;
    }

    private void exportDatabaseEntries(final Workspace workspace, final Graph graph,
                                       final Map<String, Long> localizationNodeIdMap) {
        try (final ZipInputStream inputStream = FileUtils.openZip(workspace, dataSource,
                                                                  RNALocateUpdater.DATABASE_FILE_NAME)) {
            inputStream.getNextEntry();
            final MappingIterator<DatabaseEntry> entries = FileUtils.openSeparatedValuesFile(inputStream,
                                                                                             DatabaseEntry.class, '\t',
                                                                                             true);
            while (entries.hasNext())
                exportDatabaseEntry(graph, entries.next(), localizationNodeIdMap);
        } catch (IOException e) {
            throw new ExporterFormatException(e);
        }
    }

    private void exportDatabaseEntry(final Graph graph, final DatabaseEntry entry,
                                     final Map<String, Long> localizationNodeIdMap) {
        final Long localizationNodeId = getOrCreateLocalizationNode(graph, localizationNodeIdMap,
                                                                    entry.subCellularLocalization);
        final Node rnaNode = getOrCreateRNANode(graph, entry.geneId, null, entry.geneSymbol, entry.rnaCategory);
        graph.addEdge(rnaNode, localizationNodeId, "LOCALIZED_IN", ID_KEY, entry.id, "database", entry.database,
                      "species", entry.species);
    }
}
