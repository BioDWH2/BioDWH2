package de.unibi.agbi.biodwh2.core.io.mvstore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public abstract class MVStoreModel extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = 3622312710000754490L;
    public static final String ID_FIELD = "__id";

    protected MVStoreModel() {
        super();
    }

    public void setProperty(final String key, final Object value) {
        put(key, value);
    }

    public <T> T getProperty(final String key) {
        final Object value = get(key);
        //noinspection unchecked
        return value != null ? (T) value : null;
    }

    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.writeLong(this.<MVStoreId>getProperty(ID_FIELD).getIdValue());
    }

    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        put(ID_FIELD, new MVStoreId(s.readLong()));
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
        return containsKey(key);
    }
}
