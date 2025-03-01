package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.graphics.MetaGraphImage;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.SpeciesFilter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.text.MetaGraphDynamicVisWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphExtendedStatisticsWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public abstract class GraphExporter<D extends DataSource> {
    private static final Logger LOGGER = LogManager.getLogger(GraphExporter.class);
    public static final String ID_KEY = "id";
    private static final int META_GRAPH_IMAGE_SIZE = 1024;

    protected final D dataSource;
    protected SpeciesFilter speciesFilter;
    private boolean createdOntologyProxyTermIndex = false;

    public GraphExporter(final D dataSource) {
        this.dataSource = dataSource;
        speciesFilter = new SpeciesFilter();
    }

    public abstract long getExportVersion();

    public final boolean export(final Workspace workspace) throws ExporterException {
        speciesFilter = SpeciesFilter.fromWorkspaceDataSource(workspace, dataSource);
        boolean exportSuccessful;
        try (Graph g = new Graph(dataSource.getFilePath(workspace, DataSourceFileType.PERSISTENT_GRAPH))) {
            exportSuccessful = exportGraph(workspace, g);
            if (exportSuccessful) {
                removeEmptyCollections(g);
                exportSuccessful = trySaveGraphToFile(workspace, g);
                if (exportSuccessful)
                    generateMetaGraphStatistics(workspace, g);
            }
        }
        return exportSuccessful;
    }

    protected abstract boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException;

    private void removeEmptyCollections(final Graph graph) {
        for (final String label : graph.getNodeLabels())
            if (graph.getNumberOfNodes(label) == 0)
                graph.removeNodeLabel(label);
        for (final String label : graph.getEdgeLabels())
            if (graph.getNumberOfEdges(label) == 0)
                graph.removeEdgeLabel(label);
    }

    private boolean trySaveGraphToFile(final Workspace workspace, final Graph g) {
        // TODO: remove old and unused
        boolean success = true;
        for (final var writer : workspace.getOutputFormatWriters()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting '{}' data source graph to {}", dataSource.getId(), writer.getId());
            if (!writer.write(workspace, dataSource, g))
                success = false;
        }
        return success;
    }

    private void generateMetaGraphStatistics(final Workspace workspace, final Graph g) {
        final Path metaGraphImageFilePath = dataSource.getFilePath(workspace, DataSourceFileType.META_GRAPH_IMAGE);
        final Path metaGraphStatsFilePath = dataSource.getFilePath(workspace, DataSourceFileType.META_GRAPH_STATISTICS);
        final Path metaGraphDynamicVisFilePath = dataSource.getFilePath(workspace,
                                                                        DataSourceFileType.META_GRAPH_DYNAMIC_VIS);
        if (workspace.getConfiguration().shouldSkipMetaGraphGeneration()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Skipping '{}' meta graph generation as per configuration", dataSource.getId());
            FileUtils.safeDelete(metaGraphImageFilePath);
            FileUtils.safeDelete(metaGraphStatsFilePath);
            FileUtils.safeDelete(metaGraphDynamicVisFilePath);
            return;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Generating '{}' data source meta graph", dataSource.getId());
        final MetaGraph metaGraph = new MetaGraph(g);
        if (metaGraph.getNodeLabelCount() == 0 && metaGraph.getEdgeLabelCount() == 0) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Skipping meta graph image generation of empty meta graph");
            return;
        }
        final String statistics = new MetaGraphStatisticsWriter(metaGraph).write();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(statistics);
            LOGGER.info("For extended meta graph information see: {}", metaGraphStatsFilePath);
            LOGGER.info("Exporting meta graph image to {}", metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, META_GRAPH_IMAGE_SIZE, META_GRAPH_IMAGE_SIZE);
        image.drawAndSaveImage(metaGraphImageFilePath);
        new MetaGraphExtendedStatisticsWriter(metaGraph).write(metaGraphStatsFilePath);
        final MetaGraphDynamicVisWriter visWriter = new MetaGraphDynamicVisWriter(metaGraph);
        visWriter.write(metaGraphDynamicVisFilePath);
    }

    protected final <T> void createNodesFromModels(final Graph g, final Iterable<T> models) {
        for (final T obj : models)
            g.addNodeFromModel(obj);
    }

    protected final Long getOrCreateOntologyProxyTerm(final Graph g, final String id) {
        if (!createdOntologyProxyTermIndex) {
            createdOntologyProxyTermIndex = true;
            g.addIndex(
                    IndexDescription.forNode(OntologyGraphExporter.TERM_LABEL, ID_KEY, IndexDescription.Type.UNIQUE));
            g.addIndex(IndexDescription.forNode(OntologyGraphExporter.TERM_LABEL, OntologyGraphExporter.IS_PROXY_KEY,
                                                IndexDescription.Type.NON_UNIQUE));
            return g.addNode(OntologyGraphExporter.TERM_LABEL, ID_KEY, id, OntologyGraphExporter.IS_PROXY_KEY, true)
                    .getId();
        }

        Node node = g.findNode(OntologyGraphExporter.TERM_LABEL, ID_KEY, id);
        if (node == null)
            node = g.addNode(OntologyGraphExporter.TERM_LABEL, ID_KEY, id, OntologyGraphExporter.IS_PROXY_KEY, true);
        return node.getId();
    }
}
