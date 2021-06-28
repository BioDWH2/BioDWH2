package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
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
        assertEquals("Gene", node.getLabel());
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
        Graph g = new Graph(tempFilePath);
        Node n = g.addNode("Test");
        long id = n.getId();
        g.close();
        g = new Graph(tempFilePath, true);
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

    @Test
    void indexDescriptionsTest() throws IOException {
        final Graph g = Graph.createTempGraph();
        assertEquals(0, g.indexDescriptions().length);
        g.addIndex(IndexDescription.forNode("Test", "id", false, IndexDescription.Type.UNIQUE));
        assertEquals(1, g.indexDescriptions().length);
        final IndexDescription description = g.indexDescriptions()[0];
        assertEquals("Test", description.getLabel());
        assertEquals(IndexDescription.Target.NODE, description.getTarget());
        assertEquals("id", description.getProperty());
        assertFalse(description.isArrayProperty());
        assertEquals(IndexDescription.Type.UNIQUE, description.getType());
    }

    @Test
    void indexDescriptionsAfterReopenTest() throws IOException {
        final Graph g = Graph.createTempGraph();
        g.addIndex(IndexDescription.forNode("Test", "id", false, IndexDescription.Type.UNIQUE));
        g.addIndex(IndexDescription.forNode("Test", "names", true, IndexDescription.Type.NON_UNIQUE));
        g.addIndex(IndexDescription.forEdge("ASSOCIATED_WITH", "source", false, IndexDescription.Type.NON_UNIQUE));
        assertEquals(3, g.indexDescriptions().length);
        g.close();
        final Graph reopenedGraph = new Graph(g.getFilePath(), true, true);
        final IndexDescription[] descriptions = reopenedGraph.indexDescriptions();
        assertEquals(3, descriptions.length);
        // Description 1
        Optional<IndexDescription> description = Arrays.stream(descriptions).filter(
                d -> d.getLabel().equals("Test") && d.getProperty().equals("id")).findFirst();
        assertTrue(description.isPresent());
        assertEquals(IndexDescription.Target.NODE, description.get().getTarget());
        assertFalse(description.get().isArrayProperty());
        assertEquals(IndexDescription.Type.UNIQUE, description.get().getType());
        // Description 2
        description = Arrays.stream(descriptions).filter(
                d -> d.getLabel().equals("Test") && d.getProperty().equals("names")).findFirst();
        assertTrue(description.isPresent());
        assertEquals(IndexDescription.Target.NODE, description.get().getTarget());
        assertTrue(description.get().isArrayProperty());
        assertEquals(IndexDescription.Type.NON_UNIQUE, description.get().getType());
        // Description 3
        description = Arrays.stream(descriptions).filter(
                d -> d.getLabel().equals("ASSOCIATED_WITH") && d.getProperty().equals("source")).findFirst();
        assertTrue(description.isPresent());
        assertEquals(IndexDescription.Target.EDGE, description.get().getTarget());
        assertFalse(description.get().isArrayProperty());
        assertEquals(IndexDescription.Type.NON_UNIQUE, description.get().getType());
    }
}
