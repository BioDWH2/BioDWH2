package de.unibi.agbi.biodwh2.core.model.graph;

import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("RedundantCast")
class GraphTest {
    private static final int[] TestIntArray = new int[]{0, 1, 2, 3};
    private static final Integer[] TestIntegerArray = new Integer[]{0, 1, 2, 3};
    private static final String[] TestStringArray = new String[]{"abc", "123", "\"test\",\"test2\""};

    @Test
    void testPack() throws ExporterException, IOException {
        Path tempFilePath = Files.createTempFile("graphdb_test", ".sqlite");
        Graph g = new Graph(tempFilePath.toString());
        assertEquals("S|Hello World!", g.packValue("Hello World!"));
        assertEquals("I|0", g.packValue((int) 0));
        assertEquals("I|0", g.packValue((Integer) 0));
        assertEquals("L|0", g.packValue((long) 0L));
        assertEquals("L|0", g.packValue((Long) 0L));
        assertEquals("B|1", g.packValue((byte) 1));
        assertEquals("I[]|0,1,2,3", g.packValue(TestIntArray));
        assertEquals("I[]|0,1,2,3", g.packValue(TestIntegerArray));
        assertEquals("S[]|'abc','123','\"test\",\"test2\"'", g.packValue(TestStringArray));
    }

    @Test
    void testUnpack() throws ExporterException, IOException {
        Path tempFilePath = Files.createTempFile("graphdb_test", ".sqlite");
        Graph g = new Graph(tempFilePath.toString());
        assertEquals("Hello World!", g.unpackValue(g.packValue("Hello World!")));
        assertEquals(0, g.unpackValue(g.packValue(0)));
        assertEquals(1L, g.unpackValue(g.packValue(1L)));
        assertEquals((byte) 2, g.unpackValue(g.packValue((byte) 2)));
        assertArrayEquals(TestIntegerArray, (Integer[]) g.unpackValue(g.packValue(TestIntArray)));
        assertArrayEquals(TestIntegerArray, (Integer[]) g.unpackValue(g.packValue(TestIntegerArray)));
        assertArrayEquals(TestStringArray, (String[]) g.unpackValue(g.packValue(TestStringArray)));
    }
}
