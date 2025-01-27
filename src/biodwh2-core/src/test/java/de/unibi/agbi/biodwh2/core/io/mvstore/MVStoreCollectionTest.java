package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class MVStoreCollectionTest {
    private static class TestModel1 extends MVStoreModel {
        private static final long serialVersionUID = 5094099909019692102L;

        private TestModel1() {
            super();
        }

        public static TestModel1 newTestModel() {
            TestModel1 m = new TestModel1();
            m.put(ID_FIELD, new MVStoreId().getIdValue());
            return m;
        }
    }

    private static class TestModel2 extends MVStoreModel {
        private static final long serialVersionUID = -927158315870084253L;

        private TestModel2() {
            super();
        }

        public static TestModel2 newTestModel() {
            TestModel2 m = new TestModel2();
            m.put(ID_FIELD, new MVStoreId().getIdValue());
            return m;
        }
    }

    @Test
    void multipleCollectionsTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreCollectionTest.multipleCollectionsTest", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreCollection<TestModel1> collection1 = db.getCollection("test1");
            final MVStoreCollection<TestModel2> collection2 = db.getCollection("test2");
            collection1.getIndex(MVStoreModel.ID_FIELD);
            collection2.getIndex(MVStoreModel.ID_FIELD);
            collection1.put(TestModel1.newTestModel());
            collection1.put(TestModel1.newTestModel());
            collection2.put(TestModel2.newTestModel());
            collection2.put(TestModel2.newTestModel());
        }
    }

    @Test
    void findIdsWithIndex() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreCollectionTest.findIdsWithIndex", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreCollection<TestModel1> collection = db.getCollection("test1");
            collection.getIndex(MVStoreModel.ID_FIELD, false, MVStoreIndexType.UNIQUE);
            collection.getIndex("p1", false, MVStoreIndexType.UNIQUE);
            final var a = TestModel1.newTestModel();
            a.put("p1", "1");
            final var b = TestModel1.newTestModel();
            b.put("p1", "2");
            collection.put(a);
            collection.put(b);
            assertIterableEquals(Collections.singletonList(a.getId()),
                                 collection.findIds(new String[]{"p1"}, new Comparable[]{"1"}));
            assertIterableEquals(Collections.singletonList(b.getId()),
                                 collection.findIds(new String[]{"p1"}, new Comparable[]{"2"}));
            assertIterableEquals(Collections.singletonList(a.getId()), collection.findIds("p1", "1"));
            assertIterableEquals(Collections.singletonList(b.getId()), collection.findIds("p1", "2"));
        }
    }

    @Test
    void findIdWithIndex() throws IOException {
        final Path tempFilePath = Files.createTempFile("MVStoreCollectionTest.findIdWithIndex", ".db");
        try (MVStoreDB db = new MVStoreDB(tempFilePath.toString())) {
            final MVStoreCollection<TestModel1> collection = db.getCollection("test1");
            collection.getIndex(MVStoreModel.ID_FIELD, false, MVStoreIndexType.UNIQUE);
            collection.getIndex("p1", false, MVStoreIndexType.UNIQUE);
            final var a = TestModel1.newTestModel();
            a.put("p1", "1");
            final var b = TestModel1.newTestModel();
            b.put("p1", "2");
            collection.put(a);
            collection.put(b);
            assertEquals(a.getId(), collection.findId("p1", "1"));
            assertEquals(b.getId(), collection.findId("p1", "2"));
        }
    }
}