package de.unibi.agbi.biodwh2.core.mocks.mock2.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.mocks.mock2.Mock2DataSource;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

public class Mock2GraphExporter extends GraphExporter<Mock2DataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final Mock2DataSource dataSource,
                                  final Graph graph) throws ExporterException {
        graph.setIndexColumnNames("id");
        Node node = createNode(graph, "Gene");
        node.setProperty("id", "HGNC:TLR4");
        node.setProperty("test_type_mismatch", "10");
        node = createNode(graph, "Dummy2");
        node.setProperty("id", "B");
        node.setProperty("id2", "C");
        return true;
    }
}
