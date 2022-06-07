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
        for (final IntegratedEntry entry : parseTsvFile(workspace, IntegratedEntry.class,
                                                        TissuesUpdater.INTEGRATED_FILE_NAME)) {
            final long geneNodeId = getOrCreateGene(graph, entry.geneIdentifier, entry.geneName);
            final long tissueNodeId = getOrCreateTissue(graph, entry.tissueIdentifier, entry.tissueName);
            //graph.addEdge(geneNodeId, tissueNodeId, EXPRESSED_IN_LABEL, "integrated_confidence_score",
            //              entry.confidenceScore);
        }
        for (final KnowledgeEntry entry : parseTsvFile(workspace, KnowledgeEntry.class,
                                                       TissuesUpdater.KNOWLEDGE_FILE_NAME)) {
            final long geneNodeId = getOrCreateGene(graph, entry.geneIdentifier, entry.geneName);
            final long tissueNodeId = getOrCreateTissue(graph, entry.tissueIdentifier, entry.tissueName);
            graph.addEdge(geneNodeId, tissueNodeId, EXPRESSED_IN_LABEL, "knowledge_confidence_score",
                          entry.confidenceScore, "knowledge_source", entry.sourceDatabase, "knowledge_evidence_type",
                          entry.evidenceType);
        }
        for (final ExperimentEntry entry : parseTsvFile(workspace, ExperimentEntry.class,
                                                        TissuesUpdater.EXPERIMENTS_FILE_NAME)) {
            final long geneNodeId = getOrCreateGene(graph, entry.geneIdentifier, entry.geneName);
            final long tissueNodeId = getOrCreateTissue(graph, entry.tissueIdentifier, entry.tissueName);
            //graph.addEdge(geneNodeId, tissueNodeId, EXPRESSED_IN_LABEL, "experiment_confidence_score",
            //              entry.confidenceScore, "experiment_source", entry.sourceDataset,
            //              "experiment_expression_score", entry.expressionScore);
        }
        return true;
    }

    private <T> Iterable<T> parseTsvFile(final Workspace workspace, final Class<T> typeVariableClass,
                                         final String fileName) throws ExporterException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Exporting " + fileName + "...");
        try {
            MappingIterator<T> iterator = FileUtils.openTsv(workspace, dataSource, fileName, typeVariableClass);
            return () -> iterator;
        } catch (IOException e) {
            throw new ExporterException("Failed to parse the file '" + fileName + "'", e);
        }
    }

    private long getOrCreateGene(final Graph graph, final String id, final String name) {
        Node node = graph.findNode("Gene", "id", id);
        if (node == null) {
            if (id.equals(name))
                node = graph.addNode("Gene", "id", id);
            else
                node = graph.addNode("Gene", "id", id, "name", name);
        }
        return node.getId();
    }

    private long getOrCreateTissue(final Graph graph, final String id, final String name) {
        Node node = graph.findNode("Tissue", "id", id);
        if (node == null) {
            if (id.equals(name))
                node = graph.addNode("Tissue", "id", id);
            else
                node = graph.addNode("Tissue", "id", id, "name", name);
        }
        return node.getId();
    }
}
