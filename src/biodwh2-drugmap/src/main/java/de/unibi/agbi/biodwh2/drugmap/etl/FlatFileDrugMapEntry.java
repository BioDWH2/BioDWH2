package de.unibi.agbi.biodwh2.drugmap.etl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatFileDrugMapEntry {
    private String id;
    public final Map<String, List<String>> properties;

    public FlatFileDrugMapEntry() {
        properties = new HashMap<>();
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getFirst(final String key) {
        final List<String> values = properties.get(key);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }
}
