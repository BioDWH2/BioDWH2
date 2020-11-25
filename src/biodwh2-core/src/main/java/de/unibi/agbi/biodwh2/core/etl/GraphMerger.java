package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphFileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphMerger {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMerger.class);

    public final boolean merge(final Workspace workspace, final DataSource[] dataSources,
                               final String outputFilePath) throws MergerException {
        final Graph mergedGraph = new Graph(
                outputFilePath.replace(GraphFileFormat.GRAPH_ML.extension, Graph.EXTENSION));
        for (final DataSource dataSource : dataSources)
            mergeDataSource(workspace, dataSource, mergedGraph);
        saveMergedGraph(outputFilePath, mergedGraph);
        return true;
    }

    private void mergeDataSource(final Workspace workspace, final DataSource dataSource,
                                 final Graph mergedGraph) throws MergerException {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Merging data source " + dataSource.getId());
        final String intermediateGraphFilePath = dataSource.getGraphDatabaseFilePath(workspace);
        try {
            mergedGraph.mergeDatabase(dataSource.getId(), intermediateGraphFilePath);
            dataSource.getMetadata().mergeSuccessful = true;
        } catch (GraphCacheException e) {
            throw new MergerException("Failed to merge data source " + dataSource.getId(), e);
        }
    }

    private void saveMergedGraph(final String outputFilePath, final Graph mergedGraph) {
        final GraphMLGraphWriter graphMLWriter = new GraphMLGraphWriter();
        graphMLWriter.write(outputFilePath, mergedGraph);
        mergedGraph.dispose();
    }
}
