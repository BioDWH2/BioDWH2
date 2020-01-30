package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import java.util.Date;

public class EdgeTest {
    @Test
    public void testSetProperty() {
        Node a = new Node(1);
        Node b = new Node(2);
        Edge e = new Edge(a, b, "TEST");
        e.setProperty("int_property", 0);
        e.setProperty("byte_property", 0x1b);
        e.setProperty("float_property", 0.5f);
        e.setProperty("double_property", 0.5);
        e.setProperty("double_property", true);
        e.setProperty("string_property", "Hello World!");
        e.setProperty("int_array_property", new int[]{0, 1, 2, 3, 4, 5});
        e.setProperty("date_property", new Date());
    }
}
