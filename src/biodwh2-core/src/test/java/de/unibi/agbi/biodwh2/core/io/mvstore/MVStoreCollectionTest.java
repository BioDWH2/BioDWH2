package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

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
        final Path tempFilePath = Files.createTempFile("multipleCollectionsTest", ".db");
        final MVStoreDB db = new MVStoreDB(tempFilePath.toString());
        final MVStoreCollection<TestModel1> collection1 = db.getCollection("test1");
        final MVStoreCollection<TestModel2> collection2 = db.getCollection("test2");
        collection1.getIndex(MVStoreModel.ID_FIELD);
        collection2.getIndex(MVStoreModel.ID_FIELD);
        collection1.put(TestModel1.newTestModel());
        collection1.put(TestModel1.newTestModel());
        collection2.put(TestModel2.newTestModel());
        collection2.put(TestModel2.newTestModel());
        db.close();
    }
}