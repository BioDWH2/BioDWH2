package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("RedundantCast")
class GraphTest {
    @Test
    void testPack() throws ExporterException, IOException {
        Path tempFilePath = Files.createTempFile("graphdb_test", ".sqlite");
        Graph g = new Graph(tempFilePath.toString());
        assertEquals("java.lang.String|Hello World!", g.packValue("Hello World!"));
        assertEquals("java.lang.Integer|0", g.packValue((int) 0));
        assertEquals("java.lang.Integer|0", g.packValue((Integer) 0));
        assertEquals("java.lang.Long|0", g.packValue((long) 0L));
        assertEquals("java.lang.Long|0", g.packValue((Long) 0L));
        assertEquals("java.lang.Byte|1", g.packValue((byte) 1));
        assertEquals("java.lang.Integer[]|0,1,2,3", g.packValue(new int[]{0, 1, 2, 3}));
        assertEquals("java.lang.Integer[]|0,1,2,3", g.packValue(new Integer[]{0, 1, 2, 3}));
        assertEquals("java.lang.String[]|\"abc\",\"123\",\"\\\"test\\\"\\,\\\"test2\\\"\"",
                     g.packValue(new String[]{"abc", "123", "\"test\",\"test2\""}));
    }

    @Test
    void testUnpack() throws ExporterException, IOException {
        Path tempFilePath = Files.createTempFile("graphdb_test", ".sqlite");
        Graph g = new Graph(tempFilePath.toString());
        assertEquals("Hello World!", g.unpackValue(g.packValue("Hello World!")));
        assertEquals(0, g.unpackValue(g.packValue(0)));
        assertEquals(1L, g.unpackValue(g.packValue(1L)));
        assertEquals((byte) 2, g.unpackValue(g.packValue((byte) 2)));
        assertArrayEquals(new Integer[]{0, 1, 2, 3}, (Integer[]) g.unpackValue(g.packValue(new int[]{0, 1, 2, 3})));
        assertArrayEquals(new Integer[]{0, 1, 2, 3}, (Integer[]) g.unpackValue(g.packValue(new Integer[]{0, 1, 2, 3})));
        assertArrayEquals(new String[]{"abc", "123", "\"test\",\"test2\""},
                          (String[]) g.unpackValue(g.packValue(new String[]{"abc", "123", "\"test\",\"test2\""})));
    }
}