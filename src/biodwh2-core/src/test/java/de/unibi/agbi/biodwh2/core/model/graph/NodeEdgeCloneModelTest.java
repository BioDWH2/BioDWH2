package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The fast {@link de.unibi.agbi.biodwh2.core.io.mvstore.CloneableModel} clone of {@link Node} and {@link Edge} must
 * still be independent enough that mutating a returned model never corrupts the model held in the store, exactly like
 * the previous serialization based clone.
 */
class NodeEdgeCloneModelTest {
    @Test
    void nodeCloneModelIsIndependentTest() throws IOException {
        final Path file = Files.createTempFile("NodeEdgeCloneModelTest.node", ".db");
        try (final Graph graph = new Graph(file)) {
            final Node node = graph.buildNode().withLabel("Protein").withProperty("accession", "P1").withProperty(
                    "keywords", new String[]{"a", "b"}).build();
            final Node clone = node.cloneModel();

            // Replacing, adding and removing properties on the clone must not touch the original
            clone.setProperty("accession", "MUT");
            clone.setProperty("added", 5);
            assertEquals("P1", node.getProperty("accession"));
            assertFalse(node.hasProperty("added"));

            // Array values must be copied, not shared
            ((String[]) clone.getProperty("keywords"))[0] = "X";
            assertArrayEquals(new String[]{"a", "b"}, (String[]) node.getProperty("keywords"));

            // Identity fields and the exact array component class survive the clone
            assertEquals(node.getId(), clone.getId());
            assertEquals("Protein", clone.getLabel());
            assertEquals(String[].class, clone.getProperty("keywords").getClass());
        }
    }

    @Test
    void edgeCloneModelIsIndependentTest() throws IOException {
        final Path file = Files.createTempFile("NodeEdgeCloneModelTest.edge", ".db");
        try (final Graph graph = new Graph(file)) {
            final Node a = graph.addNode("Protein", "accession", "A");
            final Node b = graph.addNode("Protein", "accession", "B");
            final Edge edge = graph.addEdge(a, b, "INTERACTS_WITH", "sources", new String[]{"exp", "pred"});
            final Edge clone = edge.cloneModel();

            ((String[]) clone.getProperty("sources"))[0] = "mutated";
            assertArrayEquals(new String[]{"exp", "pred"}, (String[]) edge.getProperty("sources"));
            assertEquals(edge.getFromId(), clone.getFromId());
            assertEquals(edge.getToId(), clone.getToId());
            assertEquals("INTERACTS_WITH", clone.getLabel());
        }
    }

    /**
     * Reading the same entity twice must yield isolated instances: mutating the first must not be visible through the
     * second, since both come from the same cached page object via the wrapper's clone.
     */
    @Test
    void graphGetReturnsIsolatedInstancesTest() throws IOException {
        final Path file = Files.createTempFile("NodeEdgeCloneModelTest.get", ".db");
        final long id;
        try (final Graph graph = new Graph(file)) {
            final Node node = graph.buildNode().withLabel("Protein").withProperty("accession", "P1").withProperty(
                    "aliases", new String[]{"x", "y"}).build();
            id = node.getId();
        }
        try (final Graph graph = new Graph(file, true)) {
            final Node first = graph.getNode(id);
            first.setProperty("accession", "MUT");
            ((String[]) first.getProperty("aliases"))[0] = "Z";

            final Node second = graph.getNode(id);
            assertEquals("P1", second.getProperty("accession"));
            assertArrayEquals(new String[]{"x", "y"}, (String[]) second.getProperty("aliases"));
        }
    }
}
