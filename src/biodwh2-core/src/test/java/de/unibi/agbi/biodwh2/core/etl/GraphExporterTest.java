package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphExporterTest {
    private static class TestGraphExporter extends GraphExporter {
        @Override
        protected boolean exportGraph(Workspace workspace, DataSource dataSource, Graph graph) {
            return false;
        }
    }

    @SuppressWarnings("unused")
    @NodeLabels({"TestModel"})
    private static class TestModel {
        @GraphProperty("name")
        public String name = "Hello World!";
        @GraphProperty("number")
        public int number = 10;
        // No GraphProperty annotation to skip this field
        public String value = "Lorem ipsum";
    }

    @Test
    void testCreateNodeFromModel() throws Exception {
        Graph g = Graph.createTempGraph();
        TestGraphExporter exporter = new TestGraphExporter();
        Node node = exporter.createNodeFromModel(g, new TestModel());
        assertTrue(node.getPropertyKeys().contains("name"));
        assertTrue(node.getPropertyKeys().contains("number"));
        assertFalse(node.getPropertyKeys().contains("value"));
        assertEquals("Hello World!", node.getProperty("name"));
        assertEquals(10, (int) node.getProperty("number"));
    }
}
