package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    @Test
    void testFindNode() throws Exception {
        Graph g = Graph.createTempGraph();
        Node node = g.addNode("Gene");
        node.setProperty("test", "Hello");
        g.update(node);
        node = g.findNode("Gene", "test", "Hello");
        assertNotNull(node);
        assertEquals("Gene", node.getLabel());
        assertTrue(node.hasProperty("test"));
        assertEquals("Hello", node.getProperty("test"));
    }

    @Test
    void nodeKeepsIdOnRetrieve() throws Exception {
        Graph g = Graph.createTempGraph();
        Node n = g.addNode("Test");
        long id = n.getId();
        n = g.getNodes().iterator().next();
        assertEquals(id, n.getId());
    }

    @Test
    void nodeKeepsIdOnUpdate() throws Exception {
        Graph g = Graph.createTempGraph();
        Node n = g.addNode("Test");
        long id = n.getId();
        n.setProperty("key", "value");
        g.update(n);
        assertEquals(id, n.getId());
    }

    @Test
    void nodeKeepsIdOnRetrieveReopenedGraph() throws Exception {
        Path tempFilePath = Files.createTempFile("graphdb_test", ".db");
        Graph g = new Graph(tempFilePath.toString());
        Node n = g.addNode("Test");
        long id = n.getId();
        g.dispose();
        g = new Graph(tempFilePath.toString(), true);
        n = g.getNodes().iterator().next();
        assertEquals(id, n.getId());
    }
}
