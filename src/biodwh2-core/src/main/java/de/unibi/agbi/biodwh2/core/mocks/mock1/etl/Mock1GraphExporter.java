package de.unibi.agbi.biodwh2.core.mocks.mock1.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.mocks.mock1.Mock1DataSource;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;

public final class Mock1GraphExporter extends GraphExporter<Mock1DataSource> {
    public Mock1GraphExporter(final Mock1DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.addIndex(IndexDescription.forNode("Gene", "hgnc_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Drug", "drugbank_id", IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode("Dummy1", "id", IndexDescription.Type.UNIQUE));
        final Node tlr4Node = graph.addNode("Gene");
        tlr4Node.setProperty("hgnc_id", "TLR4");
        tlr4Node.setProperty("array_test", new String[]{"value1", "value2", "value3"});
        tlr4Node.setProperty("string_test", "value1");
        tlr4Node.setProperty("int_test", 10);
        tlr4Node.setProperty("bool_test", true);
        tlr4Node.setProperty("test_type_mismatch", 10);
        graph.update(tlr4Node);
        Node node = graph.addNode("Gene");
        node.setProperty("hgnc_id", "IL10");
        graph.update(node);
        node = graph.addNode("Drug");
        node.setProperty("drugbank_id", "DB01183");
        graph.update(node);
        graph.addEdge(node, node, "INTERACTS");
        graph.addEdge(node, tlr4Node, "TARGETS");
        node = graph.addNode("Dummy1");
        node.setProperty("id", "A");
        node.setProperty("id2", "B");
        graph.update(node);
        node = graph.addNode("Dummy1");
        node.setProperty("id", "C");
        graph.update(node);
        return true;
    }
}
