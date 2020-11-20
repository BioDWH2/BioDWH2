package de.unibi.agbi.biodwh2.core.io.graph;

import com.sun.deploy.util.OrderedHashSet;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GraphMLPropertyFormatterTest {
    @Test
    void formatTest() {
        assertEquals("true", GraphMLPropertyFormatter.format(true));
        assertEquals("false", GraphMLPropertyFormatter.format(false));
        assertEquals("10", GraphMLPropertyFormatter.format(10));
        assertEquals("10.851", GraphMLPropertyFormatter.format(10.851f));
        assertEquals("10.851", GraphMLPropertyFormatter.format(10.851));
        assertEquals("Hello World!", GraphMLPropertyFormatter.format("Hello World!"));
        assertEquals("a", GraphMLPropertyFormatter.format('a'));
    }

    @Test
    void formatArrayTest() {
        assertEquals("[1,2,3,4,5]", GraphMLPropertyFormatter.format(new Integer[]{1, 2, 3, 4, 5}));
        assertEquals("[1,2,3,4,5]", GraphMLPropertyFormatter.format(new int[]{1, 2, 3, 4, 5}));
        assertEquals("[1,2,3,4,5]", GraphMLPropertyFormatter.format(new byte[]{1, 2, 3, 4, 5}));
        assertEquals("[1,2,3,4,5]", GraphMLPropertyFormatter.format(new short[]{1, 2, 3, 4, 5}));
        assertEquals("[1,2,3,4,5]", GraphMLPropertyFormatter.format(new long[]{1, 2, 3, 4, 5}));
        assertEquals("[true,true,false]", GraphMLPropertyFormatter.format(new boolean[]{true, true, false}));
        assertEquals("[\"a\",\"&\",\"b\"]", GraphMLPropertyFormatter.format(new char[]{'a', '&', 'b'}));
        assertEquals("[1.4,2.621,3.0,4.0,5.0]", GraphMLPropertyFormatter.format(new float[]{1.4f, 2.621f, 3, 4, 5}));
        assertEquals("[1.4,2.621,3.0,4.0,5.0]", GraphMLPropertyFormatter.format(new double[]{1.4, 2.621, 3, 4, 5}));
        assertEquals("[\"a\",\"b\",\"c\"]", GraphMLPropertyFormatter.format(new String[]{"a", "b", "c"}));
    }

    @Test
    void formatCollectionTest() {
        assertEquals("[\"a\",\"b\",\"c\"]", GraphMLPropertyFormatter.format(Arrays.asList("a", "b", "c")));
        final OrderedHashSet set = new OrderedHashSet();
        set.addAll(Arrays.asList("a", "b", "c"));
        assertEquals("[\"a\",\"b\",\"c\"]", GraphMLPropertyFormatter.format(set));
    }
}