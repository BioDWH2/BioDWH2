package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    @Test
    void testFindNode() throws Exception {
        final Graph g = Graph.createTempGraph();
        Node node = g.addNode("Gene");
        node.setProperty("test", "Hello");
        g.update(node);
        node = g.findNode("Gene", "test", "Hello");
        assertNotNull(node);
        assertEquals("Gene", node.getLabels()[0]);
        assertTrue(node.hasProperty("test"));
        assertEquals("Hello", node.getProperty("test"));
    }

    @Test
    void nodeKeepsIdOnRetrieve() throws IOException {
        final Graph g = Graph.createTempGraph();
        Node n = g.addNode("Test");
        long id = n.getId();
        n = g.getNodes().iterator().next();
        assertEquals(id, n.getId());
    }

    @Test
    void nodeKeepsIdOnUpdate() throws IOException {
        final Graph g = Graph.createTempGraph();
        final Node n = g.addNode("Test");
        long id = n.getId();
        n.setProperty("key", "value");
        g.update(n);
        assertEquals(id, n.getId());
    }

    @Test
    void nodeKeepsIdOnRetrieveReopenedGraph() throws IOException {
        final Path tempFilePath = Files.createTempFile("graphdb_test", ".db");
        Graph g = new Graph(tempFilePath.toString());
        Node n = g.addNode("Test");
        long id = n.getId();
        g.close();
        g = new Graph(tempFilePath.toString(), true);
        n = g.getNodes().iterator().next();
        assertEquals(id, n.getId());
    }

    @Test
    void numberOfNodesAndEdges() throws IOException {
        final Graph g = Graph.createTempGraph();
        final Node n1 = g.addNode("Test");
        final Node n2 = g.addNode("Test");
        g.addEdge(n1, n2, "LABEL1");
        g.addEdge(n1, n2, "LABEL2");
        g.addEdge(n1, n2, "LABEL3");
        assertEquals(2, g.getNumberOfNodes());
        assertEquals(3, g.getNumberOfEdges());
    }

    @Test
    void differentEdgeLabelsAreRetrievedCorrectly() throws IOException {
        final Graph g = Graph.createTempGraph();
        final Node n1 = g.addNode("Test");
        final Node n2 = g.addNode("Test");
        final Edge e1 = g.addEdge(n1, n2, "LABEL1");
        final Edge e2 = g.addEdge(n1, n2, "LABEL2");
        final Edge e3 = g.addEdge(n1, n2, "LABEL3");
        assertEquals(e1.getId(), g.getEdges("LABEL1").iterator().next().getId());
        assertEquals(e2.getId(), g.getEdges("LABEL2").iterator().next().getId());
        assertEquals(e3.getId(), g.getEdges("LABEL3").iterator().next().getId());
    }

    @Test
    void getEdgesReturnsAllEdges() throws IOException {
        final Graph g = Graph.createTempGraph();
        final Node n1 = g.addNode("Test");
        final Node n2 = g.addNode("Test");
        final Edge e1 = g.addEdge(n1, n2, "LABEL1");
        final Edge e2 = g.addEdge(n1, n2, "LABEL2");
        final Edge e3 = g.addEdge(n1, n2, "LABEL3");
        final Set<Long> remainingIds = new HashSet<>(Arrays.asList(e1.getId(), e2.getId(), e3.getId()));
        assertEquals(3, remainingIds.size());
        for (Edge e : g.getEdges())
            remainingIds.remove(e.getId());
        assertEquals(0, remainingIds.size());
    }

    @Test
    void findEdgeByLongFromId() throws IOException {
        final Graph g = Graph.createTempGraph();
        final Node n1 = g.addNode("Test");
        final Node n2 = g.addNode("Test");
        final Edge e1 = g.addEdge(n1, n2, "LABEL1");
        final Edge foundEdge = g.findEdge("LABEL1", Edge.FROM_ID_FIELD, n1.getId());
        assertNotNull(foundEdge);
        assertEquals(e1.getId(), foundEdge.getId());
    }
}
