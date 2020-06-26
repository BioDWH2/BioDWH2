package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    @Test
    void synchronizedNodePropertiesAreLoadedCorrectly() throws IOException {
        Graph g = Graph.createTempGraph();
        Node node = g.addNode("Test");
        node.setProperty("string_array", new String[]{"Hello", "World", "!"});
        node.setProperty("int", 345);
        node.setProperty("int_array", new int[]{4, 6, 10});
        assertEquals(1, g.getNumberOfNodes());
        assertEquals(1, g.getNumberOfCachedNodes());
        g.synchronize(true);
        assertEquals(1, g.getNumberOfNodes());
        assertEquals(0, g.getNumberOfCachedNodes());
        node = g.getNode(1);
        assertNotNull(node);
        assertArrayEquals(new String[]{"Hello", "World", "!"}, node.<String[]>getProperty("string_array"));
        assertEquals(345, node.<Integer>getProperty("int"));
        assertArrayEquals(new Integer[]{4, 6, 10}, node.<Integer[]>getProperty("int_array"));
    }

    @Test
    void nodeInstancesCanBeModifiedAfterSynchronization() throws IOException {
        Graph g = Graph.createTempGraph();
        Node node = g.addNode("Test");
        // Node is synchronized from memory to disk
        g.synchronize(true);
        assertEquals(0, g.getNumberOfCachedNodes());
        // Set a property on a synchronized instance
        node.setProperty("property", "value");
        assertEquals(1, g.getNumberOfCachedNodes());
        // Assert property on instance
        assertEquals("value", node.getProperty("property"));
        // Assert property on instance loaded from memory cache
        node = g.getNode(1);
        assertNotNull(node);
        assertEquals("value", node.getProperty("property"));
        g.synchronize(true);
        assertEquals(0, g.getNumberOfCachedNodes());
        // Assert property on instance loaded from disk cache
        node = g.getNode(1);
        assertNotNull(node);
        assertEquals("value", node.getProperty("property"));
    }
}
