package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
    @NodeLabels("TestModel")
    private static class TestModel {
        @GraphProperty("text")
        String text;
        @GraphArrayProperty(value = "array", arrayDelimiter = ";")
        String arrayText;
    }

    @Test
    void testNodeFromModel() throws Exception {
        Graph g = Graph.createTempGraph();
        TestModel model = new TestModel();
        model.text = "Hello";
        model.arrayText = "A;123;BC";
        Node node = g.addNodeFromModel(model);
        assertEquals("Hello", node.getProperty("text"));
        assertArrayEquals(new String[]{"A", "123", "BC"}, node.getProperty("array"));
    }
}
