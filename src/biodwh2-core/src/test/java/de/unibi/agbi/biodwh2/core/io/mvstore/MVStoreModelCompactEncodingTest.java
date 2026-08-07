package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Round-trip and backwards-compatibility tests for the compact {@link MVStoreModelValueType} entity encoding.
 */
class MVStoreModelCompactEncodingTest {
    /**
     * Minimal concrete model with a package visible no-arg constructor, so the compact decoder can instantiate it.
     */
    static final class CompactTestModel extends MVStoreModel {
        private static final long serialVersionUID = 1L;

        CompactTestModel() {
            super();
        }

        static CompactTestModel withId(final long id) {
            final CompactTestModel model = new CompactTestModel();
            model.put(ID_FIELD, id);
            return model;
        }
    }

    @Test
    void roundTripsAllValueTypesAcrossReopenTest() throws IOException {
        final Path file = Files.createTempFile("MVStoreModelCompactEncodingTest.roundTrip", ".db");
        final long id = 42;
        try (final MVStoreDB db = new MVStoreDB(file.toString())) {
            final MVStoreCollection<CompactTestModel> collection = db.getCollection("test");
            final CompactTestModel model = CompactTestModel.withId(id);
            model.put("string", "value");
            model.put("empty", "");
            model.put("unicode", "proteïne é中文");
            model.put("bool", Boolean.TRUE);
            model.put("byte", (byte) -7);
            model.put("short", (short) -1234);
            model.put("char", 'Z');
            model.put("int", -42);
            model.put("int_big", 2000000000);
            model.put("long", 1234567890123456789L);
            model.put("long_neg", -9876543210L);
            model.put("float", 3.5f);
            model.put("double", -2.718281828459045);
            model.put("string_arr", new String[]{"a", "", "long value", null});
            model.put("int_arr", new int[]{-1, 0, 1, 2000000000});
            model.put("long_arr", new long[]{-1L, 9999999999L});
            model.put("bool_arr", new boolean[]{true, false, true});
            model.put("double_arr", new double[]{1.5, -2.5, 0.0});
            model.put("boxed_int_arr", new Integer[]{1, null, 3});
            model.put("boxed_double_arr", new Double[]{1.1, 2.2});
            model.put("null_valued", null);
            collection.put(model);
        }
        // Reopen so the values are decoded from disk, not returned from an in-memory cache
        try (final MVStoreDB db = new MVStoreDB(file.toString())) {
            final MVStoreCollection<CompactTestModel> collection = db.getCollection("test");
            final CompactTestModel model = collection.get(id);
            assertNotNull(model);
            assertEquals("value", model.get("string"));
            assertEquals("", model.get("empty"));
            assertEquals("proteïne é中文", model.get("unicode"));
            assertEquals(Boolean.TRUE, model.get("bool"));
            assertEquals((byte) -7, model.get("byte"));
            assertEquals((short) -1234, model.get("short"));
            assertEquals('Z', model.get("char"));
            assertEquals(-42, model.get("int"));
            assertEquals(2000000000, model.get("int_big"));
            assertEquals(1234567890123456789L, model.get("long"));
            assertEquals(-9876543210L, model.get("long_neg"));
            assertEquals(3.5f, model.get("float"));
            assertEquals(-2.718281828459045, model.get("double"));
            assertArrayEquals(new String[]{"a", "", "long value", null}, (String[]) model.get("string_arr"));
            assertArrayEquals(new int[]{-1, 0, 1, 2000000000}, (int[]) model.get("int_arr"));
            assertArrayEquals(new long[]{-1L, 9999999999L}, (long[]) model.get("long_arr"));
            assertArrayEquals(new boolean[]{true, false, true}, (boolean[]) model.get("bool_arr"));
            assertArrayEquals(new double[]{1.5, -2.5, 0.0}, (double[]) model.get("double_arr"));
            assertArrayEquals(new Integer[]{1, null, 3}, (Integer[]) model.get("boxed_int_arr"));
            assertArrayEquals(new Double[]{1.1, 2.2}, (Double[]) model.get("boxed_double_arr"));
            // Exact array component classes must survive
            assertEquals(String[].class, model.get("string_arr").getClass());
            assertEquals(int[].class, model.get("int_arr").getClass());
            assertEquals(Integer[].class, model.get("boxed_int_arr").getClass());
            // A property whose value was null is simply not stored
            assertFalse(model.hasProperty("null_valued"));
            assertEquals(id, (long) model.getId());
        }
    }

    /**
     * A collection created by an older version stored its entities as generic Java serialized objects and did not know
     * about the model class or key dictionary. Such records must keep being readable, and new records written into the
     * same collection use the compact encoding, so a graph may legitimately hold a mix of both.
     */
    @Test
    void readsLegacyJavaSerializedRecordsAndMixedFormatTest() throws IOException {
        final Path file = Files.createTempFile("MVStoreModelCompactEncodingTest.legacy", ".db");
        final long legacyId = 1;
        final long newId = 2;
        // Session 1: write a record the old way, straight through the generic object data type, without a dictionary
        // or model class, exactly as a pre-dictionary version would have persisted it
        try (final MVStoreDB db = new MVStoreDB(file.toString())) {
            final MVMapWrapper<Long, Object> rawMap = db.openMap("test");
            final CompactTestModel legacy = CompactTestModel.withId(legacyId);
            legacy.put("accession", "P-legacy");
            legacy.put("names", new String[]{"old", "record"});
            rawMap.put(legacyId, legacy);
        }
        // Session 2: open as a collection (compact codec). The legacy record must still read, then add a compact record
        try (final MVStoreDB db = new MVStoreDB(file.toString())) {
            final MVStoreCollection<CompactTestModel> collection = db.getCollection("test");
            final CompactTestModel legacy = collection.get(legacyId);
            assertNotNull(legacy);
            assertEquals("P-legacy", legacy.get("accession"));
            assertArrayEquals(new String[]{"old", "record"}, (String[]) legacy.get("names"));

            final CompactTestModel fresh = CompactTestModel.withId(newId);
            fresh.put("accession", "P-new");
            fresh.put("count", 99);
            collection.put(fresh);
        }
        // Session 3: both the legacy and the compact record must read correctly from the same collection
        try (final MVStoreDB db = new MVStoreDB(file.toString())) {
            final MVStoreCollection<CompactTestModel> collection = db.getCollection("test");
            assertEquals(2, collection.size());
            final CompactTestModel legacy = collection.get(legacyId);
            assertEquals("P-legacy", legacy.get("accession"));
            assertArrayEquals(new String[]{"old", "record"}, (String[]) legacy.get("names"));
            final CompactTestModel fresh = collection.get(newId);
            assertEquals("P-new", fresh.get("accession"));
            assertEquals(99, fresh.get("count"));
        }
    }

    /**
     * The compact key dictionary assigns ids by first-seen order and must keep them stable across reopen, even when
     * later records introduce additional keys.
     */
    @Test
    void keyDictionaryStaysStableAcrossReopenTest() throws IOException {
        final Path file = Files.createTempFile("MVStoreModelCompactEncodingTest.dictionary", ".db");
        try (final MVStoreDB db = new MVStoreDB(file.toString())) {
            final MVStoreCollection<CompactTestModel> collection = db.getCollection("test");
            final CompactTestModel first = CompactTestModel.withId(1);
            first.put("a", "1");
            first.put("b", "2");
            collection.put(first);
        }
        // Reopen and add a record with a new key, then a value under an existing key
        try (final MVStoreDB db = new MVStoreDB(file.toString())) {
            final MVStoreCollection<CompactTestModel> collection = db.getCollection("test");
            final CompactTestModel second = CompactTestModel.withId(2);
            second.put("b", "3");
            second.put("c", "4");
            collection.put(second);
        }
        try (final MVStoreDB db = new MVStoreDB(file.toString())) {
            final MVStoreCollection<CompactTestModel> collection = db.getCollection("test");
            final CompactTestModel first = collection.get(1);
            assertEquals("1", first.get("a"));
            assertEquals("2", first.get("b"));
            final CompactTestModel second = collection.get(2);
            assertEquals("3", second.get("b"));
            assertEquals("4", second.get("c"));
        }
    }
}
