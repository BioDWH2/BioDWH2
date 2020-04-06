package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import org.junit.jupiter.api.Test;

import java.util.Date;

class EdgeTest {
    @Test
    void testSetProperty() throws ExporterException {
        Node a = new Node(null, 1, true);
        Node b = new Node(null, 2, true);
        Edge e = new Edge(null, 1, a.getId(), b.getId(), "TEST");
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
