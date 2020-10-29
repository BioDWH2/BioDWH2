package de.unibi.agbi.biodwh2.core.mocks.mock2.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.mocks.mock2.Mock2DataSource;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

public final class Mock2GraphExporter extends GraphExporter<Mock2DataSource> {
    public Mock2GraphExporter(final Mock2DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.setNodeIndexPropertyKeys("id");
        Node node = createNode(graph, "Gene");
        node.setProperty("id", "HGNC:TLR4");
        node.setProperty("test_type_mismatch", "10");
        graph.update(node);
        node = createNode(graph, "Dummy2");
        node.setProperty("id", "B");
        node.setProperty("id2", "C");
        graph.update(node);
        return true;
    }
}
