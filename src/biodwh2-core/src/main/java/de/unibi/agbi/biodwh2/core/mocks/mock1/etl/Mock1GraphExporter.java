package de.unibi.agbi.biodwh2.core.mocks.mock1.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.mocks.mock1.Mock1DataSource;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

public class Mock1GraphExporter extends GraphExporter<Mock1DataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final Mock1DataSource dataSource,
                                  final Graph graph) throws ExporterException {
        graph.setIndexColumnNames("hgnc_id");
        Node node = createNode(graph, "Gene");
        node.setProperty("hgnc_id", "TLR4");
        node.setProperty("array_test", new String[]{"value1", "value2", "value3"});
        node.setProperty("string_test", "value1");
        node.setProperty("int_test", 10);
        node.setProperty("bool_test", true);
        node.setProperty("test_type_mismatch", 10);
        node = createNode(graph, "Gene");
        node.setProperty("hgnc_id", "IL10");
        node = createNode(graph, "Dummy1");
        node.setProperty("id", "A");
        node.setProperty("id2", "B");
        node = createNode(graph, "Dummy1");
        node.setProperty("id", "C");
        return true;
    }
}
