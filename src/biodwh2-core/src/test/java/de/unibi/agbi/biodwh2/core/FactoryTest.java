package de.unibi.agbi.biodwh2.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class FactoryTest {
    @Test
    public void instanceTest() {
        assertNotNull(Factory.getInstance());
    }

    @Test
    public void getInterfaceImplementationsTest() {
        List<Class<TestInterface>> result = Factory.getInstance().getImplementations(TestInterface.class);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).equals(TestClass1.class) || result.get(0).equals(TestClass2.class));
        assertTrue(result.get(1).equals(TestClass1.class) || result.get(1).equals(TestClass2.class));
    }

    @Test
    public void getClassImplementationsTest() {
        List<Class<TestClass>> result = Factory.getInstance().getImplementations(TestClass.class);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).equals(TestClass3.class));
    }

    private interface TestInterface {
    }

    private abstract class TestClass {
    }

    private class TestClass1 implements TestInterface {
    }

    private class TestClass2 implements TestInterface {
    }

    private class TestClass3 extends TestClass {
    }
}