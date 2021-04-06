package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassMappingTest {
    @Test
    void loadClassMapping() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        assertEquals(2, mapping.fields.length);
        assertEquals(1, mapping.booleanFields.length);
        assertEquals(3, mapping.arrayFields.length);
        assertArrayEquals(new String[]{"A", "B"}, mapping.labels);
        assertEquals("id", mapping.fields[0].propertyName);
        assertFalse(mapping.fields[0].ignoreEmpty);
        assertEquals("ignore_empty_string", mapping.fields[1].propertyName);
        assertTrue(mapping.fields[1].ignoreEmpty);
    }

    @Test
    void setNodeProperties() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        instance.id = "A1234";
        instance.enabled = true;
        instance.array = "test;abc;efg";
        final Node node = Node.newNode(mapping.labels);
        mapping.setNodeProperties(node, instance);
        assertEquals(instance.id, node.getProperty("id"));
        assertEquals(instance.enabled, node.getProperty("enabled"));
        assertArrayEquals(new String[]{"test", "abc", "efg"}, node.getProperty("array"));
    }

    @Test
    void setNodePropertiesWithCustomArrays() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        instance.arrayWithCustomDelimiter = "test|abc|efg";
        instance.quotedArray = "\"test\";\"abc\";\"efg\"";
        final Node node = Node.newNode(mapping.labels);
        mapping.setNodeProperties(node, instance);
        assertArrayEquals(new String[]{"test", "abc", "efg"}, node.getProperty("array_with_custom_delimiter"));
        assertArrayEquals(new String[]{"test", "abc", "efg"}, node.getProperty("quoted_array"));
    }

    @Test
    void setNodePropertiesIgnoreNull() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        final Node node = Node.newNode(mapping.labels);
        mapping.setNodeProperties(node, instance);
        assertFalse(node.hasProperty("id"));
    }

    @Test
    void setNodePropertiesIgnoreEmpty() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        instance.ignoreEmptyString = "";
        final Node node = Node.newNode(mapping.labels);
        mapping.setNodeProperties(node, instance);
        assertFalse(node.hasProperty("ignore_empty_string"));
        instance.ignoreEmptyString = "t";
        mapping.setNodeProperties(node, instance);
        assertTrue(node.hasProperty("ignore_empty_string"));
    }

    @NodeLabels({"A", "B"})
    private static class TestClass {
        @GraphProperty("id")
        public String id;
        @GraphProperty(value = "ignore_empty_string", ignoreEmpty = true)
        public String ignoreEmptyString;
        @GraphBooleanProperty("enabled")
        public Boolean enabled;
        @GraphArrayProperty("array")
        public String array;
        @GraphArrayProperty(value = "array_with_custom_delimiter", arrayDelimiter = "|")
        public String arrayWithCustomDelimiter;
        @GraphArrayProperty(value = "quoted_array", quotedArrayElements = true)
        public String quotedArray;
    }
}