package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.graphics.MetaGraphImage;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.DataSourceFileType;
import de.unibi.agbi.biodwh2.core.model.SpeciesFilter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.meta.MetaGraph;
import de.unibi.agbi.biodwh2.core.text.MetaGraphDynamicVisWriter;
import de.unibi.agbi.biodwh2.core.text.MetaGraphStatisticsWriter;
import de.unibi.agbi.biodwh2.core.text.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.*;

public abstract class GraphExporter<D extends DataSource> {
    private static final Logger LOGGER = LogManager.getLogger(GraphExporter.class);
    public static final String ID_KEY = "id";
    static final String DEPENDENCY_NODE_PROPERTY = "__dependency";
    private static final int META_GRAPH_IMAGE_SIZE = 1024;

    protected final D dataSource;
    protected SpeciesFilter speciesFilter;

    public GraphExporter(final D dataSource) {
        this.dataSource = dataSource;
        speciesFilter = new SpeciesFilter();
    }

    public abstract long getExportVersion();

    public final boolean export(final Workspace workspace) throws ExporterException {
        final List<Integer> speciesFilterIds = new ArrayList<>();
        final var workspaceSpeciesFilter = workspace.getConfiguration().getGlobalProperties().speciesFilter;
        if (workspaceSpeciesFilter != null)
            Collections.addAll(speciesFilterIds, workspaceSpeciesFilter);
        final var dataSourceSpeciesFilter = dataSource.<List<Integer>>getProperty(workspace, "speciesFilter");
        if (dataSourceSpeciesFilter != null)
            speciesFilterIds.addAll(dataSourceSpeciesFilter);
        speciesFilter = new SpeciesFilter(speciesFilterIds);
        boolean exportSuccessful;
        try (Graph g = new Graph(dataSource.getFilePath(workspace, DataSourceFileType.PERSISTENT_GRAPH))) {
            exportSuccessful = mergeDependencies(workspace, g);
            if (exportSuccessful) {
                exportSuccessful = exportGraph(workspace, g);
                if (exportSuccessful) {
                    exportSuccessful = trySaveGraphToFile(workspace, g);
                    if (exportSuccessful)
                        generateMetaGraphStatistics(workspace, g);
                }
            }
        }
        return exportSuccessful;
    }

    private boolean mergeDependencies(final Workspace workspace, final Graph graph) {
        final String[] dependencyIds = dataSource.getDependencies();
        if (dependencyIds == null || dependencyIds.length == 0)
            return true;
        final Map<String, Object> dependencyProperty = new HashMap<>();
        dependencyProperty.put(DEPENDENCY_NODE_PROPERTY, true);
        for (final String dependencyId : dependencyIds) {
            try (Graph dependencyGraph = new Graph(
                    DataSource.getFilePath(workspace, dependencyId, DataSourceFileType.PERSISTENT_GRAPH), true, true)) {
                final long numberOfNodes = dependencyGraph.getNumberOfNodes();
                final long numberOfEdges = dependencyGraph.getNumberOfEdges();
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Adding data source dependency '{}' [{} nodes, {} edges]", dependencyId, numberOfNodes,
                                numberOfEdges);
                graph.mergeDatabase("", dependencyGraph, (nodeCounter) -> {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("\tNode progress: {}", TextUtils.getProgressText(nodeCounter, numberOfNodes));
                }, (edgeCounter) -> {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("\tEdge progress: {}", TextUtils.getProgressText(edgeCounter, numberOfEdges));
                }, false, dependencyProperty);
            }
        }
        return true;
    }

    protected abstract boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException;

    private boolean trySaveGraphToFile(final Workspace workspace, final Graph g) {
        final GraphMLGraphWriter writer = new GraphMLGraphWriter();
        if (workspace.getConfiguration().shouldSkipGraphMLExport()) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Skipping '{}' GraphML export as per configuration", dataSource.getId());
            writer.removeOldExport(workspace, dataSource);
            return true;
        }
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Save '{}' data source graph to GraphML", dataSource.getId());
        return writer.write(workspace, dataSource, g);
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
            LOGGER.info("Exporting meta graph image to {}", metaGraphImageFilePath);
        }
        final MetaGraphImage image = new MetaGraphImage(metaGraph, META_GRAPH_IMAGE_SIZE, META_GRAPH_IMAGE_SIZE);
        image.drawAndSaveImage(metaGraphImageFilePath);
        FileUtils.writeTextToUTF8File(metaGraphStatsFilePath, statistics);
        final MetaGraphDynamicVisWriter visWriter = new MetaGraphDynamicVisWriter(metaGraph);
        visWriter.write(metaGraphDynamicVisFilePath);
    }

    protected final <T> void createNodesFromModels(final Graph g, final Iterable<T> models) {
        for (final T obj : models)
            g.addNodeFromModel(obj);
    }
}
