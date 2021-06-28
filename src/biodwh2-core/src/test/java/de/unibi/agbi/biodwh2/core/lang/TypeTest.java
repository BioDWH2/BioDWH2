package de.unibi.agbi.biodwh2.core.lang;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {
    @Test
    void typeFromObject() {
        assertType(Type.fromObject("Hello world"), String.class, null);
        assertType(Type.fromObject(true), Boolean.class, null);
        assertType(Type.fromObject(10), Integer.class, null);
        assertType(Type.fromObject(10.5f), Float.class, null);
        assertType(Type.fromObject(10.5), Double.class, null);
        assertType(Type.fromObject(10L), Long.class, null);
        assertType(Type.fromObject('x'), Character.class, null);
        assertType(Type.fromObject(new String[]{"a", "bc"}), String[].class, String.class);
        assertType(Type.fromObject(new int[]{10, 5}), int[].class, int.class);
        assertType(Type.fromObject(new Integer[]{10, 5}), Integer[].class, Integer.class);
        assertType(Type.fromObject(new ArrayList<>(Arrays.asList("a", "bc"))), ArrayList.class, String.class);
        assertType(Type.fromObject(new ArrayList<>(Arrays.asList(10, 54))), ArrayList.class, Integer.class);
        assertType(Type.fromObject(new ArrayList<>(Arrays.asList(true, false))), ArrayList.class, Boolean.class);
        final Set<Integer> testSet = new HashSet<>(Arrays.asList(10, 54));
        assertType(Type.fromObject(testSet), HashSet.class, Integer.class);
    }

    private void assertType(final Type type, final Class<?> expectedType, final Class<?> expectedComponentType) {
        assertEquals(expectedType, type.getType());
        assertEquals(expectedComponentType, type.getComponentType());
        assertEquals(expectedComponentType != null, type.isList());
    }
}