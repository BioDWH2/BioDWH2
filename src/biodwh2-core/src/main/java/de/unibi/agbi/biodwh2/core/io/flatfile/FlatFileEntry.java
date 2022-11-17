package de.unibi.agbi.biodwh2.core.io.flatfile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlatFileEntry {
    public final List<List<KeyValuePair>> properties = new ArrayList<>();

    public FlatFileEntry.KeyValuePair getProperty(final String tag) {
        for (final List<FlatFileEntry.KeyValuePair> property : properties)
            if (tag.equals(property.get(0).key))
                return property.get(0);
        return null;
    }

    public List<FlatFileEntry.KeyValuePair> getProperties(final String tag) {
        return properties.stream().filter((p) -> tag.equals(p.get(0).key)).map((p) -> p.get(0)).collect(
                Collectors.toList());
    }

    public List<FlatFileEntry.KeyValuePair> getComplexProperty(final String tag) {
        for (final List<FlatFileEntry.KeyValuePair> property : properties)
            if (tag.equals(property.get(0).key))
                return property;
        return null;
    }

    public List<List<FlatFileEntry.KeyValuePair>> getComplexProperties(final String tag) {
        return properties.stream().filter((p) -> tag.equals(p.get(0).key)).collect(Collectors.toList());
    }

    public static class KeyValuePair {
        public final String key;
        public final String value;

        public KeyValuePair(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "KeyValuePair{" + "key='" + key + "', value='" + value + "'}";
        }
    }
}
