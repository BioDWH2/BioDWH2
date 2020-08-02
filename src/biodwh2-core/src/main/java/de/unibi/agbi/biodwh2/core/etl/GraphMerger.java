package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import de.unibi.agbi.biodwh2.core.exceptions.MergerException;
import de.unibi.agbi.biodwh2.core.io.graph.GraphMLGraphWriter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GraphMerger extends Merger {
    private static final Logger LOGGER = LoggerFactory.getLogger(Merger.class);

    @Override
    public final boolean merge(final Workspace workspace, final List<DataSource> dataSources,
                               final String outputFilePath) throws MergerException {
        final Graph mergedGraph = new Graph(outputFilePath.replace(GraphMLGraphWriter.EXTENSION, Graph.EXTENSION));
        for (final DataSource dataSource : dataSources) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Merging data source " + dataSource.getId());
            final String intermediateGraphFilePath = dataSource.getGraphDatabaseFilePath(workspace);
            try {
                mergedGraph.mergeDatabase(intermediateGraphFilePath);
                dataSource.getMetadata().mergeSuccessful = true;
            } catch (GraphCacheException e) {
                throw new MergerException("Failed to merge data source " + dataSource.getId(), e);
            }
        }
        final GraphMLGraphWriter graphMLWriter = new GraphMLGraphWriter();
        graphMLWriter.write(outputFilePath, mergedGraph);
        mergedGraph.dispose();
        return true;
    }
}
