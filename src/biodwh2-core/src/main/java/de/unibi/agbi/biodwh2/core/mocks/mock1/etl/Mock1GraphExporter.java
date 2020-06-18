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
        Node tlr4Node = createNode(graph, "Gene");
        tlr4Node.setProperty("hgnc_id", "TLR4");
        tlr4Node.setProperty("array_test", new String[]{"value1", "value2", "value3"});
        tlr4Node.setProperty("string_test", "value1");
        tlr4Node.setProperty("int_test", 10);
        tlr4Node.setProperty("bool_test", true);
        tlr4Node.setProperty("test_type_mismatch", 10);
        Node node = createNode(graph, "Gene");
        node.setProperty("hgnc_id", "IL10");
        node = createNode(graph, "Drug");
        node.setProperty("drugbank_id", "DB01183");
        graph.addEdge(node, tlr4Node, "TARGETS");
        node = createNode(graph, "Dummy1");
        node.setProperty("id", "A");
        node.setProperty("id2", "B");
        node = createNode(graph, "Dummy1");
        node.setProperty("id", "C");
        return true;
    }
}
