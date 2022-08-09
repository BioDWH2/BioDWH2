package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphViewTest {
    @Test
    void testNodeView() throws Exception {
        final Graph g = Graph.createTempGraph();
        g.addNode("Gene", "id", 1);
        g.addNode("Gene", "id", 2);
        g.addNode("Gene", "id", 3);
        g.addNode("Gene", "id", 4);
        g.addNode("Protein", "id", 1);
        g.addNode("Protein", "id", 2);
        g.addNode("Protein", "id", 3);
        g.addNode("Protein", "id", 4);
        final GraphView view = new GraphView(g, new String[]{"Protein"}, new String[]{});
        assertEquals(4, view.getNumberOfNodes());
        assertEquals(4, view.getNumberOfNodes("Protein"));
        assertEquals(0, view.getNumberOfNodes("Gene"));
        assertEquals(4, StreamSupport.stream(view.getNodes().spliterator(), false).count());
        for (final Node node : view.getNodes()) {
            assertEquals("Protein", node.getLabel());
        }
    }

    @Test
    void testEdgeView() throws Exception {
        final Graph g = Graph.createTempGraph();
        final Node a = g.addNode("Gene", "id", 1);
        final Node b = g.addNode("Protein", "id", 2);
        g.addEdge(a, b, "TEST", "w", 5);
        g.addEdge(b, a, "TEST", "w", 1);
        g.addEdge(b, a, "HAS_GENE");
        final GraphView view = new GraphView(g, new String[]{"Gene", "Protein"}, new String[]{"TEST"});
        assertEquals(2, view.getNumberOfNodes());
        assertEquals(1, view.getNumberOfNodes("Protein"));
        assertEquals(1, view.getNumberOfNodes("Gene"));
        assertEquals(2, view.getNumberOfEdges());
        assertEquals(2, view.getNumberOfEdges("TEST"));
        assertEquals(0, view.getNumberOfEdges("HAS_GENE"));
        assertEquals(2, StreamSupport.stream(view.getEdges().spliterator(), false).count());
        for (final Edge edge : view.getEdges()) {
            assertEquals("TEST", edge.getLabel());
        }
    }
}