package de.unibi.agbi.biodwh2.core.io.mvstore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class MVStoreModel implements Serializable {
    private static final long serialVersionUID = 3622312710000754490L;
    public static final String ID_FIELD = "__id";
    private Map<String, Object> properties;

    protected MVStoreModel() {
        properties = new HashMap<>();
    }

    public final void put(final String key, final Object value) {
        properties.put(key, value);
    }

    public final void setProperty(final String key, final Object value) {
        properties.put(key, value);
    }

    public final Object get(final String key) {
        return properties.get(key);
    }

    public final <T> T getProperty(final String key) {
        final Object value = properties.get(key);
        //noinspection unchecked
        return value != null ? (T) value : null;
    }

    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.writeObject(properties);
    }

    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        //noinspection unchecked
        properties = (HashMap<String, Object>) s.readObject();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + super.toString();
    }

    public final Long getId() {
        return this.getProperty(ID_FIELD);
    }

    public final boolean hasProperty(final String key) {
        return properties.containsKey(key);
    }

    public final Set<String> keySet() {
        return properties.keySet();
    }

    /**
     * Copy this model's properties into another model of the same kind, for {@link CloneableModel#cloneModel()}.
     * <p>
     * The clone must be independent enough that mutating it does not corrupt the model still held in the store's page
     * cache: the property map is a fresh copy, and array values are copied one level deep. Every other value type
     * BioDWH2 stores ({@link String}, boxed primitives) is immutable and can be shared safely. This mirrors the one
     * level array cloning the map wrapper already performs for direct array values, and avoids the far more expensive
     * Java serialization round-trip previously used to clone entities.
     *
     * @param target the model to copy the properties into
     */
    protected final void copyStateInto(final MVStoreModel target) {
        final Map<String, Object> copy = new HashMap<>(Math.max(16, properties.size() * 2));
        for (final Map.Entry<String, Object> entry : properties.entrySet())
            copy.put(entry.getKey(), cloneValue(entry.getValue()));
        target.properties = copy;
    }

    private static Object cloneValue(final Object value) {
        if (value == null || !value.getClass().isArray())
            return value;
        final int length = Array.getLength(value);
        final Object copy = Array.newInstance(value.getClass().getComponentType(), length);
        System.arraycopy(value, 0, copy, 0, length);
        return copy;
    }
}
