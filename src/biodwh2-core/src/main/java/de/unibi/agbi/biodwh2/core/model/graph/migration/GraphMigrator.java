package de.unibi.agbi.biodwh2.core.model.graph.migration;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.nio.file.Path;

public final class GraphMigrator {
    public static Integer peekVersion(final Path filePath) {
        Integer result = null;
        try (MVStore store = new MVStore.Builder().compress().fileName(filePath.toString()).readOnly().open()) {
            if (store.hasMap("metadata")) {
                final MVMap<String, Object> map = store.openMap("metadata");
                result = map.containsKey("version") ? (Integer) map.get("version") : null;
            }
        }
        return result;
    }
}
