package de.unibi.agbi.biodwh2.core.model.graph;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassMappingTest {
    @Test
    void loadClassMapping() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        assertEquals(4, mapping.fields.length);
        assertEquals(1, mapping.booleanFields.length);
        assertEquals(3, mapping.arrayFields.length);
        assertEquals("A", mapping.label);
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
        final Node node = Node.newNode(mapping.label);
        mapping.setModelProperties(node, instance);
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
        final Node node = Node.newNode(mapping.label);
        mapping.setModelProperties(node, instance);
        assertArrayEquals(new String[]{"test", "abc", "efg"}, node.getProperty("array_with_custom_delimiter"));
        assertArrayEquals(new String[]{"test", "abc", "efg"}, node.getProperty("quoted_array"));
    }

    @Test
    void setNodePropertiesWithOptionalQuotedArrays() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        instance.quotedArray = "test;abc;efg";
        final Node node = Node.newNode(mapping.label);
        mapping.setModelProperties(node, instance);
        assertArrayEquals(new String[]{"test", "abc", "efg"}, node.getProperty("quoted_array"));
    }

    @Test
    void setNodePropertiesIgnoreNull() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        final Node node = Node.newNode(mapping.label);
        mapping.setModelProperties(node, instance);
        assertFalse(node.hasProperty("id"));
    }

    @Test
    void setNodePropertiesIgnoreEmpty() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        instance.ignoreEmptyString = "";
        final Node node = Node.newNode(mapping.label);
        mapping.setModelProperties(node, instance);
        assertFalse(node.hasProperty("ignore_empty_string"));
        instance.ignoreEmptyString = "t";
        mapping.setModelProperties(node, instance);
        assertTrue(node.hasProperty("ignore_empty_string"));
    }

    @Test
    void setNodePropertiesEmptyPlaceholder() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        instance.emptyPlaceholder = "NA";
        final Node node = Node.newNode(mapping.label);
        mapping.setModelProperties(node, instance);
        assertFalse(node.hasProperty("null_on_empty_placeholder"));
        instance.emptyPlaceholder = "t";
        mapping.setModelProperties(node, instance);
        assertTrue(node.hasProperty("null_on_empty_placeholder"));
    }

    @Test
    void transformListToArray() {
        final ClassMapping mapping = new ClassMapping(TestClass.class);
        final TestClass instance = new TestClass();
        instance.listTransformedToArray = new ArrayList<>();
        instance.listTransformedToArray.add("1234");
        instance.listTransformedToArray.add("abcd");
        instance.listTransformedToArray.add("hello");
        final Node node = Node.newNode(mapping.label);
        mapping.setModelProperties(node, instance);
        assertTrue(node.hasProperty("list_transformed_to_array"));
        assertEquals(String[].class, node.getProperty("list_transformed_to_array").getClass());
        assertArrayEquals(new String[]{"1234", "abcd", "hello"}, node.getProperty("list_transformed_to_array"));
    }

    @Test
    void arrayWithMultipleSeparators() {
        final ClassMapping mapping = new ClassMapping(TestClassMultiSeparator.class);
        final TestClassMultiSeparator instance = new TestClassMultiSeparator();
        instance.array = "a";
        final Node node = Node.newNode("test");
        mapping.setModelProperties(node, instance);
        assertArrayEquals(new String[]{"a"}, node.getProperty("array"));
        instance.array = "a; bcd; d";
        mapping.setModelProperties(node, instance);
        assertArrayEquals(new String[]{"a", "bcd", "d"}, node.getProperty("array"));
        instance.array = "a|bcd|d";
        mapping.setModelProperties(node, instance);
        assertArrayEquals(new String[]{"a", "bcd", "d"}, node.getProperty("array"));
        instance.array = "a; bcd|d; x";
        mapping.setModelProperties(node, instance);
        assertArrayEquals(new String[]{"a", "bcd", "d", "x"}, node.getProperty("array"));
    }

    @Test
    void arrayWithType() {
        final ClassMapping mapping = new ClassMapping(TestClassArrayType.class);
        final TestClassArrayType instance = new TestClassArrayType();
        instance.intArray = "5";
        final Node node = Node.newNode("test");
        mapping.setModelProperties(node, instance);
        assertArrayEquals(new Integer[]{5}, node.getProperty("int_array"));
        instance.intArray = "1;10;04969048";
        mapping.setModelProperties(node, instance);
        assertArrayEquals(new Integer[]{1, 10, 4969048}, node.getProperty("int_array"));
    }

    @Test
    void enumType() {
        final ClassMapping mapping = new ClassMapping(TestClassEnumType.class);
        final TestClassEnumType instance = new TestClassEnumType();
        instance.a = TestEnum.VALUE_1;
        instance.b = TestEnum.VALUE_2;
        final Node node = Node.newNode("test");
        mapping.setModelProperties(node, instance);
        assertEquals("VALUE_1", node.getProperty("a"));
        assertEquals("VALUE_2", node.getProperty("b"));
    }

    @GraphNodeLabel("A")
    private static class TestClass {
        @GraphProperty("id")
        public String id;
        @GraphProperty(value = "ignore_empty_string", ignoreEmpty = true)
        public String ignoreEmptyString;
        @GraphProperty(value = "null_on_empty_placeholder", emptyPlaceholder = "NA")
        public String emptyPlaceholder;
        @GraphBooleanProperty("enabled")
        public Boolean enabled;
        @GraphArrayProperty("array")
        public String array;
        @GraphArrayProperty(value = "array_with_custom_delimiter", arrayDelimiter = "|")
        public String arrayWithCustomDelimiter;
        @GraphArrayProperty(value = "quoted_array", quotedArrayElements = true)
        public String quotedArray;
        @GraphProperty(value = "list_transformed_to_array", transformation = ValueTransformation.COLLECTION_TO_ARRAY)
        public List<String> listTransformedToArray;
    }

    private static class TestClassMultiSeparator {
        @GraphArrayProperty(value = "array", arrayDelimiter = {"; ", "|"})
        public String array;
    }

    private static class TestClassArrayType {
        @GraphArrayProperty(value = "int_array", type = GraphArrayProperty.Type.Int)
        public String intArray;
    }

    private static class TestClassEnumType {
        @GraphProperty(value = "a", transformation = ValueTransformation.ENUM_TO_STRING)
        public TestEnum a;
        @GraphProperty(value = "b", transformation = ValueTransformation.ENUM_TO_STRING)
        public TestEnum b;
    }

    private enum TestEnum {
        VALUE_1,
        VALUE_2
    }
}