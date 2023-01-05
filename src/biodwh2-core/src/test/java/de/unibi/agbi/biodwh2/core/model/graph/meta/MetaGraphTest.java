package de.unibi.agbi.biodwh2.core.model.graph.meta;

import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MetaGraphTest {
    @Test
    void testMetaGraph() throws IOException {
        try (final Graph graph = Graph.createTempGraph()) {
            final Node node1 = graph.addNode("N1");
            final Node node2 = graph.addNode("N1");
            final Node node3 = graph.addNode("N1");
            final Node node4 = graph.addNode("N2");
            final Node node5 = graph.addNode("N3");
            final Node node6 = graph.addNode("N4");
            graph.addEdge(node1, node2, "E1");
            graph.addEdge(node1, node3, "E1");
            graph.addEdge(node1, node4, "E2");
            graph.addEdge(node1, node5, "E3");
            graph.addEdge(node1, node6, "E4");
            graph.addEdge(node2, node6, "E4");
            graph.addEdge(node3, node4, "E5");
            graph.addEdge(node3, node5, "E6");
            final MetaGraph metaGraph = new MetaGraph(graph);
            assertFalse(metaGraph.isMappedGraph());
            assertEquals(4, metaGraph.getNodeLabelCount());
            assertEquals(6, metaGraph.getEdgeLabelCount());
            assertEquals(2, metaGraph.getEdge("E1|N1|N1").count);
            assertEquals(1, metaGraph.getEdge("E2|N1|N2").count);
            assertEquals(1, metaGraph.getEdge("E3|N1|N3").count);
            assertEquals(2, metaGraph.getEdge("E4|N1|N4").count);
        }
    }
}