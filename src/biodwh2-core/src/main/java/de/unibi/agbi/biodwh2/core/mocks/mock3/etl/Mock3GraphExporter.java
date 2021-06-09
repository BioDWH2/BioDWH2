package de.unibi.agbi.biodwh2.core.mocks.mock3.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.mocks.mock3.Mock3DataSource;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

public class Mock3GraphExporter extends GraphExporter<Mock3DataSource> {
    public Mock3GraphExporter(final Mock3DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode("Test", "id", IndexDescription.Type.UNIQUE));
        for (long i = 0; i < 10_000; i++) {
            final Node n = graph.addNode("Test", "id", i, "name", "testnode-" + i);
            if (i > 0)
                graph.addEdge(n, graph.findNode("Test", "id", i - 1), "HAS_PREVIOUS");
        }
        return true;
    }
}
