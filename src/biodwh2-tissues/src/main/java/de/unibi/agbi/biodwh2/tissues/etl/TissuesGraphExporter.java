package de.unibi.agbi.biodwh2.tissues.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.tissues.TissuesDataSource;
import de.unibi.agbi.biodwh2.tissues.model.ExperimentEntry;
import de.unibi.agbi.biodwh2.tissues.model.IntegratedEntry;
import de.unibi.agbi.biodwh2.tissues.model.KnowledgeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TissuesGraphExporter extends GraphExporter<TissuesDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TissuesGraphExporter.class);
    private static final String EXPRESSED_IN_LABEL = "EXPRESSED_IN";

    public TissuesGraphExporter(final TissuesDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode("Gene", "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Tissue", "id", false, IndexDescription.Type.UNIQUE));
        final Map<String, Long> geneIdNodeIdMap = exportGenes(workspace, graph);
        final Map<String, Long> tissueIdNodeIdMap = exportTissues(workspace, graph);
        exportEdges(workspace, graph, geneIdNodeIdMap, tissueIdNodeIdMap);
        return true;
    }

    private Map<String, Long> exportGenes(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting genes...");
        final Map<String, String> genes = new HashMap<>();
        for (final IntegratedEntry entry : parseTsvFile(workspace, IntegratedEntry.class,
                                                        TissuesUpdater.INTEGRATED_FILE_NAME)) {
            updateEntityEntry(genes, entry.geneIdentifier, entry.geneName);
        }
        for (final KnowledgeEntry entry : parseTsvFile(workspace, KnowledgeEntry.class,
                                                       TissuesUpdater.KNOWLEDGE_FILE_NAME)) {
            updateEntityEntry(genes, entry.geneIdentifier, entry.geneName);
        }
        for (final ExperimentEntry entry : parseTsvFile(workspace, ExperimentEntry.class,
                                                        TissuesUpdater.EXPERIMENTS_FILE_NAME)) {
            updateEntityEntry(genes, entry.geneIdentifier, entry.geneName);
        }
        final Map<String, Long> geneIdNodeIdMap = new HashMap<>();
        for (final String id : genes.keySet()) {
            final String name = genes.get(id);
            final Node node;
            if (id.equals(name))
                node = graph.addNode("Gene", "id", id);
            else
                node = graph.addNode("Gene", "id", id, "name", name);
            geneIdNodeIdMap.put(id, node.getId());
        }
        return geneIdNodeIdMap;
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        try {
            MappingIterator<T> iterator = FileUtils.openTsv(workspace, dataSource, fileName, typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private void updateEntityEntry(final Map<String, String> entries, final String id, final String name) {
        final String geneName = entries.get(id);
        entries.put(id, geneName == null ? name : geneName);
    }

    private Map<String, Long> exportTissues(final Workspace workspace, final Graph graph) {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting tissues...");
        final Map<String, String> tissues = new HashMap<>();
        for (final IntegratedEntry entry : parseTsvFile(workspace, IntegratedEntry.class,
                                                        TissuesUpdater.INTEGRATED_FILE_NAME)) {
            updateEntityEntry(tissues, entry.tissueIdentifier, entry.tissueName);
        }
        for (final KnowledgeEntry entry : parseTsvFile(workspace, KnowledgeEntry.class,
                                                       TissuesUpdater.KNOWLEDGE_FILE_NAME)) {
            updateEntityEntry(tissues, entry.tissueIdentifier, entry.tissueName);
        }
        for (final ExperimentEntry entry : parseTsvFile(workspace, ExperimentEntry.class,
                                                        TissuesUpdater.EXPERIMENTS_FILE_NAME)) {
            updateEntityEntry(tissues, entry.tissueIdentifier, entry.tissueName);
        }
        final Map<String, Long> tissueIdNodeIdMap = new HashMap<>();
        for (final String id : tissues.keySet()) {
            final String name = tissues.get(id);
            final Node node;
            if (id.equals(name))
                node = graph.addNode("Tissue", "id", id);
            else
                node = graph.addNode("Tissue", "id", id, "name", name);
            tissueIdNodeIdMap.put(id, node.getId());
        }
        return tissueIdNodeIdMap;
    }

    private void exportEdges(final Workspace workspace, final Graph graph, final Map<String, Long> geneIdNodeIdMap,
                             final Map<String, Long> tissueIdNodeIdMap) {
        graph.beginEdgeIndicesDelay(EXPRESSED_IN_LABEL);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + TissuesUpdater.INTEGRATED_FILE_NAME + "...");
        for (final IntegratedEntry entry : parseTsvFile(workspace, IntegratedEntry.class,
                                                        TissuesUpdater.INTEGRATED_FILE_NAME)) {
            graph.addEdge(geneIdNodeIdMap.get(entry.geneIdentifier), tissueIdNodeIdMap.get(entry.tissueIdentifier),
                          EXPRESSED_IN_LABEL, "integrated_confidence_score", entry.confidenceScore);
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + TissuesUpdater.KNOWLEDGE_FILE_NAME + "...");
        for (final KnowledgeEntry entry : parseTsvFile(workspace, KnowledgeEntry.class,
                                                       TissuesUpdater.KNOWLEDGE_FILE_NAME)) {
            graph.addEdge(geneIdNodeIdMap.get(entry.geneIdentifier), tissueIdNodeIdMap.get(entry.tissueIdentifier),
                          EXPRESSED_IN_LABEL, "knowledge_confidence_score", entry.confidenceScore, "knowledge_source",
                          entry.sourceDatabase, "knowledge_evidence_type", entry.evidenceType);
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + TissuesUpdater.EXPERIMENTS_FILE_NAME + "...");
        for (final ExperimentEntry entry : parseTsvFile(workspace, ExperimentEntry.class,
                                                        TissuesUpdater.EXPERIMENTS_FILE_NAME)) {
            graph.addEdge(geneIdNodeIdMap.get(entry.geneIdentifier), tissueIdNodeIdMap.get(entry.tissueIdentifier),
                          EXPRESSED_IN_LABEL, "experiment_confidence_score", entry.confidenceScore, "experiment_source",
                          entry.sourceDataset, "experiment_expression_score", entry.expressionScore);
        }
        graph.endEdgeIndicesDelay(EXPRESSED_IN_LABEL);
    }
}
