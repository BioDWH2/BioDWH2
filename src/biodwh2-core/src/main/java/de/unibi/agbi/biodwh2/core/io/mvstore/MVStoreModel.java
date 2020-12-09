package de.unibi.agbi.biodwh2.core.io.mvstore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public abstract class MVStoreModel implements Serializable {
    private static final long serialVersionUID = 3622312710000754490L;
    public static final String ID_FIELD = "__id";
    private HashMap<String, Object> properties;
    private HashMap<String, Object> modifiedProperties;

    protected MVStoreModel() {
        properties = new HashMap<>();
        modifiedProperties = new HashMap<>();
    }

    HashMap<String, Object> getModifiedProperties() {
        return modifiedProperties;
    }

    void resetModifiedProperties() {
        modifiedProperties.clear();
    }

    public final void put(final String key, final Object value) {
        setProperty(key, value);
    }

    public final void setProperty(final String key, final Object value) {
        final Object oldValue = properties.get(key);
        if (oldValue != null)
            modifiedProperties.put(key, oldValue);
        properties.put(key, value);
    }

    protected final void setPropertyUnmodified(final String key, final Object value) {
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
        modifiedProperties = new HashMap<>();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + super.toString();
    }

    public final MVStoreId getId() {
        return this.getProperty(ID_FIELD);
    }

    public final long getIdValue() {
        return getId().getIdValue();
    }

    public final boolean hasProperty(final String key) {
        return properties.containsKey(key);
    }

    public final Set<String> keySet() {
        return properties.keySet();
    }
}
