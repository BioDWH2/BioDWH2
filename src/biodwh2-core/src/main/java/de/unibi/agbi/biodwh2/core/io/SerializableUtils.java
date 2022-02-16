package de.unibi.agbi.biodwh2.core.io;

import java.io.*;

public final class SerializableUtils {
    private SerializableUtils() {
    }

    public static <T extends Serializable> T clone(T obj) {
        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            new ObjectOutputStream(output).writeObject(obj);
            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(output.toByteArray()));
            //noinspection unchecked
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
