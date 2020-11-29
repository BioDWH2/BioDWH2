package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MVStoreIndexTest {
    private static class TestModel extends MVStoreModel {
        private static final long serialVersionUID = -8910940086802399786L;

        private TestModel() {
            super();
        }

        public static TestModel newTestModel(final int index, final String label) {
            TestModel m = new TestModel();
            m.put(ID_FIELD, new MVStoreId());
            m.put("index", index);
            m.put("label", label);
            return m;
        }
    }

    @Test
    void pagesTest() throws IOException {
        final Path tempFilePath = Files.createTempFile("test", ".db");
        MVStoreDB db = new MVStoreDB(tempFilePath.toString());
        MVStoreCollection<TestModel> models = db.getCollection("models");
        MVStoreIndex indexIndex = models.getIndex("index", false);
        MVStoreIndex labelIndex = models.getIndex("label", false);
        for (int i = 0; i < 1000; i++) {
            final TestModel model = TestModel.newTestModel(i, "TEST");
            models.put(model);
            assertEquals(i + 1, labelIndex.find("TEST").size());
            final Set<Long> foundIds = indexIndex.find(i);
            assertEquals(1, foundIds.size());
            assertEquals(model.getIdValue(), foundIds.stream().findFirst().get());
        }
        db.close();
    }
}