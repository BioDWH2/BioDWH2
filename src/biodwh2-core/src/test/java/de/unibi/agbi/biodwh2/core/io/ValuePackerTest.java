package de.unibi.agbi.biodwh2.core.io;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("RedundantCast")
class ValuePackerTest {
    private static final int[] TestIntArray = new int[]{0, 1, 2, 3};
    private static final Integer[] TestIntegerArray = new Integer[]{0, 1, 2, 3};
    private static final String[] TestStringArray = new String[]{"abc", "123", "\"test\",\"test2\""};

    @Test
    void testPack() {
        assertEquals("S|Hello World!", ValuePacker.packValue("Hello World!"));
        assertEquals("I|0", ValuePacker.packValue((int) 0));
        assertEquals("I|0", ValuePacker.packValue((Integer) 0));
        assertEquals("L|0", ValuePacker.packValue((long) 0L));
        assertEquals("L|0", ValuePacker.packValue((Long) 0L));
        assertEquals("B|1", ValuePacker.packValue((byte) 1));
        assertEquals("I[]|0,1,2,3", ValuePacker.packValue(TestIntArray));
        assertEquals("I[]|0,1,2,3", ValuePacker.packValue(TestIntegerArray));
        assertEquals("S[]|'abc','123','\"test\",\"test2\"'", ValuePacker.packValue(TestStringArray));
    }

    @Test
    void testUnpack() {
        assertEquals("Hello World!", ValuePacker.unpackValue(ValuePacker.packValue("Hello World!")));
        assertEquals(0, ValuePacker.unpackValue(ValuePacker.packValue(0)));
        assertEquals(1L, ValuePacker.unpackValue(ValuePacker.packValue(1L)));
        assertEquals((byte) 2, ValuePacker.unpackValue(ValuePacker.packValue((byte) 2)));
        assertArrayEquals(TestIntegerArray, (Integer[]) ValuePacker.unpackValue(ValuePacker.packValue(TestIntArray)));
        assertArrayEquals(TestIntegerArray,
                          (Integer[]) ValuePacker.unpackValue(ValuePacker.packValue(TestIntegerArray)));
        assertArrayEquals(TestStringArray, (String[]) ValuePacker.unpackValue(ValuePacker.packValue(TestStringArray)));
    }
}
