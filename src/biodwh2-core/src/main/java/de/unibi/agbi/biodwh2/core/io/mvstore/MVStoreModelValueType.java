package de.unibi.agbi.biodwh2.core.io.mvstore;

import org.h2.mvstore.DataType;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.ObjectDataType;
import org.h2.mvstore.WriteBuffer;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Compact {@link DataType} for {@link MVStoreModel} values (nodes and edges).
 * <p>
 * The previous storage serialized every entity as a {@link java.util.HashMap} through Java object serialization, which
 * repeated the full property-key strings and a bulky serialization envelope in every single record. A minimal edge took
 * ~400 bytes before compression. This type instead stores only:
 * <ul>
 *     <li>a one byte format marker,</li>
 *     <li>the number of properties,</li>
 *     <li>for each property a small integer key id (see {@link PropertyKeyDictionary}) and a type tagged, varint packed
 *     value.</li>
 * </ul>
 * The property keys themselves live once in the collection dictionary instead of once per record, and values use their
 * natural binary form. The model is rebuilt on read from the dictionary and the collection's model class.
 * <p>
 * <b>Backwards compatibility:</b> graphs written before this type existed stored their entities in the old
 * Java-serialized form, whose values always begin with {@link ObjectDataType}'s {@code TYPE_SERIALIZED_OBJECT} tag
 * (19). {@link #read(ByteBuffer)} detects the format from the first byte: the new format starts with
 * {@link #COMPACT_MARKER}, anything else is delegated to a generic {@link ObjectDataType}, so old records keep being
 * read correctly. New records are always written in the compact form, so a graph may legitimately contain a mix while
 * being upgraded.
 */
final class MVStoreModelValueType implements DataType {
    /** First byte of a compact record. Distinct from every {@link ObjectDataType} tag, in particular from the value 19 that begins an old Java-serialized entity. */
    private static final int COMPACT_MARKER = 0xBD;

    // Value type tags
    private static final byte T_NULL = 0;
    private static final byte T_STRING = 1;
    private static final byte T_BOOL_FALSE = 2;
    private static final byte T_BOOL_TRUE = 3;
    private static final byte T_BYTE = 4;
    private static final byte T_SHORT = 5;
    private static final byte T_CHAR = 6;
    private static final byte T_INT = 7;
    private static final byte T_LONG = 8;
    private static final byte T_FLOAT = 9;
    private static final byte T_DOUBLE = 10;
    private static final byte T_ARRAY = 11;
    private static final byte T_FALLBACK = 12;

    // Array component type ids. 0-10 hold boxed/object components (elements are written value-tagged so nulls survive),
    // 20-27 hold primitive components (elements are written raw).
    private static final int C_STRING = 0;
    private static final int C_CHARSEQUENCE = 1;
    private static final int C_CHARACTER = 2;
    private static final int C_BYTE = 3;
    private static final int C_SHORT = 4;
    private static final int C_INTEGER = 5;
    private static final int C_LONG = 6;
    private static final int C_FLOAT = 7;
    private static final int C_DOUBLE = 8;
    private static final int C_BOOLEAN = 9;
    private static final int C_OBJECT = 10;
    private static final int C_P_BOOLEAN = 20;
    private static final int C_P_BYTE = 21;
    private static final int C_P_CHAR = 22;
    private static final int C_P_SHORT = 23;
    private static final int C_P_INT = 24;
    private static final int C_P_LONG = 25;
    private static final int C_P_FLOAT = 26;
    private static final int C_P_DOUBLE = 27;

    private final PropertyKeyDictionary dictionary;
    private final ObjectDataType objectDataType = new ObjectDataType();
    private volatile Constructor<?> modelConstructor;

    MVStoreModelValueType(final PropertyKeyDictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Set the concrete {@link MVStoreModel} subclass this collection stores, so that {@link #read(ByteBuffer)} can
     * rebuild instances of it. Old, Java-serialized records carry their own class and do not need this.
     */
    void setModelClass(final String className) {
        if (className == null)
            return;
        try {
            final Constructor<?> constructor = Class.forName(className).getDeclaredConstructor();
            constructor.setAccessible(true);
            modelConstructor = constructor;
        } catch (ReflectiveOperationException e) {
            throw new MVStoreIndexException("Failed to resolve model class '" + className + "' for compact decoding", e);
        }
    }

    boolean hasModelClass() {
        return modelConstructor != null;
    }

    private MVStoreModel newModel() {
        final Constructor<?> constructor = modelConstructor;
        if (constructor == null)
            throw new MVStoreIndexException("Cannot decode a compact record without a known model class");
        try {
            return (MVStoreModel) constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new MVStoreIndexException("Failed to instantiate model class for compact decoding", e);
        }
    }

    @Override
    public void write(final WriteBuffer buff, final Object obj) {
        if (!(obj instanceof MVStoreModel)) {
            // Not expected for entity maps, but keep behaviour defined
            objectDataType.write(buff, obj);
            return;
        }
        final MVStoreModel model = (MVStoreModel) obj;
        buff.put((byte) COMPACT_MARKER);
        final Set<String> keys = model.keySet();
        int count = 0;
        for (final String key : keys)
            if (model.get(key) != null)
                count++;
        buff.putVarInt(count);
        for (final String key : keys) {
            final Object value = model.get(key);
            if (value == null)
                continue;
            buff.putVarInt(dictionary.idOf(key));
            encodeValue(buff, value);
        }
    }

    @Override
    public void write(final WriteBuffer buff, final Object[] obj, final int len, final boolean key) {
        for (int i = 0; i < len; i++)
            write(buff, obj[i]);
    }

    @Override
    public Object read(final ByteBuffer buff) {
        final int start = buff.position();
        final int first = buff.get() & 0xff;
        if (first != COMPACT_MARKER) {
            // Old format record (or any generic value): let the generic type decode the whole self-describing value
            buff.position(start);
            return objectDataType.read(buff);
        }
        final MVStoreModel model = newModel();
        final int count = DataUtils.readVarInt(buff);
        for (int i = 0; i < count; i++) {
            final int keyId = DataUtils.readVarInt(buff);
            final String key = dictionary.keyOf(keyId);
            model.put(key, decodeValue(buff));
        }
        return model;
    }

    @Override
    public void read(final ByteBuffer buff, final Object[] obj, final int len, final boolean key) {
        for (int i = 0; i < len; i++)
            obj[i] = read(buff);
    }

    private void encodeValue(final WriteBuffer buff, final Object value) {
        if (value == null) {
            buff.put(T_NULL);
        } else if (value instanceof String) {
            buff.put(T_STRING);
            writeString(buff, (String) value);
        } else if (value instanceof Boolean) {
            buff.put((Boolean) value ? T_BOOL_TRUE : T_BOOL_FALSE);
        } else if (value instanceof Byte) {
            buff.put(T_BYTE).put((Byte) value);
        } else if (value instanceof Short) {
            buff.put(T_SHORT).putVarInt(zigZag((Short) value));
        } else if (value instanceof Character) {
            buff.put(T_CHAR).putVarInt((Character) value);
        } else if (value instanceof Integer) {
            buff.put(T_INT).putVarInt(zigZag((Integer) value));
        } else if (value instanceof Long) {
            buff.put(T_LONG).putVarLong(zigZag((Long) value));
        } else if (value instanceof Float) {
            buff.put(T_FLOAT).putInt(Float.floatToRawIntBits((Float) value));
        } else if (value instanceof Double) {
            buff.put(T_DOUBLE).putLong(Double.doubleToRawLongBits((Double) value));
        } else if (value.getClass().isArray()) {
            encodeArray(buff, value);
        } else {
            encodeFallback(buff, value);
        }
    }

    private Object decodeValue(final ByteBuffer buff) {
        final byte tag = buff.get();
        switch (tag) {
            case T_NULL:
                return null;
            case T_STRING:
                return readString(buff);
            case T_BOOL_FALSE:
                return Boolean.FALSE;
            case T_BOOL_TRUE:
                return Boolean.TRUE;
            case T_BYTE:
                return buff.get();
            case T_SHORT:
                return (short) unZigZag(DataUtils.readVarInt(buff));
            case T_CHAR:
                return (char) DataUtils.readVarInt(buff);
            case T_INT:
                return unZigZag(DataUtils.readVarInt(buff));
            case T_LONG:
                return unZigZag(DataUtils.readVarLong(buff));
            case T_FLOAT:
                return Float.intBitsToFloat(buff.getInt());
            case T_DOUBLE:
                return Double.longBitsToDouble(buff.getLong());
            case T_ARRAY:
                return decodeArray(buff);
            case T_FALLBACK:
                return decodeFallback(buff);
            default:
                throw new MVStoreIndexException("Unknown compact value tag " + tag);
        }
    }

    private void encodeArray(final WriteBuffer buff, final Object array) {
        final Class<?> component = array.getClass().getComponentType();
        final int componentId = componentId(component);
        if (componentId < 0) {
            encodeFallback(buff, array);
            return;
        }
        final int length = Array.getLength(array);
        buff.put(T_ARRAY).put((byte) componentId).putVarInt(length);
        if (component.isPrimitive()) {
            writePrimitiveArray(buff, array, componentId, length);
        } else {
            for (int i = 0; i < length; i++)
                encodeValue(buff, Array.get(array, i));
        }
    }

    private Object decodeArray(final ByteBuffer buff) {
        final int componentId = buff.get() & 0xff;
        final Class<?> component = componentClass(componentId);
        final int length = DataUtils.readVarInt(buff);
        final Object array = Array.newInstance(component, length);
        if (component.isPrimitive()) {
            readPrimitiveArray(buff, array, componentId, length);
        } else {
            for (int i = 0; i < length; i++)
                Array.set(array, i, decodeValue(buff));
        }
        return array;
    }

    private void writePrimitiveArray(final WriteBuffer buff, final Object array, final int componentId,
                                     final int length) {
        switch (componentId) {
            case C_P_BOOLEAN:
                for (final boolean v : (boolean[]) array)
                    buff.put((byte) (v ? 1 : 0));
                break;
            case C_P_BYTE:
                buff.put((byte[]) array);
                break;
            case C_P_CHAR:
                for (final char v : (char[]) array)
                    buff.putVarInt(v);
                break;
            case C_P_SHORT:
                for (final short v : (short[]) array)
                    buff.putVarInt(zigZag(v));
                break;
            case C_P_INT:
                for (final int v : (int[]) array)
                    buff.putVarInt(zigZag(v));
                break;
            case C_P_LONG:
                for (final long v : (long[]) array)
                    buff.putVarLong(zigZag(v));
                break;
            case C_P_FLOAT:
                for (final float v : (float[]) array)
                    buff.putInt(Float.floatToRawIntBits(v));
                break;
            case C_P_DOUBLE:
                for (final double v : (double[]) array)
                    buff.putLong(Double.doubleToRawLongBits(v));
                break;
            default:
                throw new MVStoreIndexException("Unknown primitive array component id " + componentId);
        }
    }

    private void readPrimitiveArray(final ByteBuffer buff, final Object array, final int componentId,
                                    final int length) {
        switch (componentId) {
            case C_P_BOOLEAN:
                for (int i = 0; i < length; i++)
                    ((boolean[]) array)[i] = buff.get() != 0;
                break;
            case C_P_BYTE:
                buff.get((byte[]) array);
                break;
            case C_P_CHAR:
                for (int i = 0; i < length; i++)
                    ((char[]) array)[i] = (char) DataUtils.readVarInt(buff);
                break;
            case C_P_SHORT:
                for (int i = 0; i < length; i++)
                    ((short[]) array)[i] = (short) unZigZag(DataUtils.readVarInt(buff));
                break;
            case C_P_INT:
                for (int i = 0; i < length; i++)
                    ((int[]) array)[i] = unZigZag(DataUtils.readVarInt(buff));
                break;
            case C_P_LONG:
                for (int i = 0; i < length; i++)
                    ((long[]) array)[i] = unZigZag(DataUtils.readVarLong(buff));
                break;
            case C_P_FLOAT:
                for (int i = 0; i < length; i++)
                    ((float[]) array)[i] = Float.intBitsToFloat(buff.getInt());
                break;
            case C_P_DOUBLE:
                for (int i = 0; i < length; i++)
                    ((double[]) array)[i] = Double.longBitsToDouble(buff.getLong());
                break;
            default:
                throw new MVStoreIndexException("Unknown primitive array component id " + componentId);
        }
    }

    private void encodeFallback(final WriteBuffer buff, final Object value) {
        final byte[] bytes = javaSerialize(value);
        buff.put(T_FALLBACK).putVarInt(bytes.length).put(bytes);
    }

    private Object decodeFallback(final ByteBuffer buff) {
        final int length = DataUtils.readVarInt(buff);
        final byte[] bytes = new byte[length];
        buff.get(bytes);
        return javaDeserialize(bytes);
    }

    @Override
    public int getMemory(final Object obj) {
        if (!(obj instanceof MVStoreModel))
            return objectDataType.getMemory(obj);
        final MVStoreModel model = (MVStoreModel) obj;
        int memory = 32;
        for (final String key : model.keySet()) {
            final Object value = model.get(key);
            if (value == null)
                continue;
            memory += 2 + estimateValueMemory(value);
        }
        return memory;
    }

    private int estimateValueMemory(final Object value) {
        if (value instanceof String)
            return 2 + ((String) value).length();
        if (value instanceof Long || value instanceof Double)
            return 9;
        if (value instanceof Integer || value instanceof Float)
            return 5;
        if (value instanceof Boolean || value instanceof Byte)
            return 1;
        if (value instanceof Short || value instanceof Character)
            return 3;
        if (value.getClass().isArray())
            return 8 + Array.getLength(value) * 4;
        return 32;
    }

    @Override
    public int compare(final Object a, final Object b) {
        // Values of the entity map are never ordered by the store; this is only reached through the assertion in
        // MVMap.replace(), which the collection does not use. Identity is the common case, otherwise fall back to a
        // stable byte comparison.
        if (a == b)
            return 0;
        if (a == null)
            return -1;
        if (b == null)
            return 1;
        final byte[] ba = javaSerialize(a);
        final byte[] bb = javaSerialize(b);
        final int min = Math.min(ba.length, bb.length);
        for (int i = 0; i < min; i++) {
            final int diff = (ba[i] & 0xff) - (bb[i] & 0xff);
            if (diff != 0)
                return diff;
        }
        return ba.length - bb.length;
    }

    private static int componentId(final Class<?> component) {
        if (component == String.class)
            return C_STRING;
        if (component == CharSequence.class)
            return C_CHARSEQUENCE;
        if (component == Character.class)
            return C_CHARACTER;
        if (component == Byte.class)
            return C_BYTE;
        if (component == Short.class)
            return C_SHORT;
        if (component == Integer.class)
            return C_INTEGER;
        if (component == Long.class)
            return C_LONG;
        if (component == Float.class)
            return C_FLOAT;
        if (component == Double.class)
            return C_DOUBLE;
        if (component == Boolean.class)
            return C_BOOLEAN;
        if (component == Object.class)
            return C_OBJECT;
        if (component == boolean.class)
            return C_P_BOOLEAN;
        if (component == byte.class)
            return C_P_BYTE;
        if (component == char.class)
            return C_P_CHAR;
        if (component == short.class)
            return C_P_SHORT;
        if (component == int.class)
            return C_P_INT;
        if (component == long.class)
            return C_P_LONG;
        if (component == float.class)
            return C_P_FLOAT;
        if (component == double.class)
            return C_P_DOUBLE;
        return -1;
    }

    private static Class<?> componentClass(final int componentId) {
        switch (componentId) {
            case C_STRING:
                return String.class;
            case C_CHARSEQUENCE:
                return CharSequence.class;
            case C_CHARACTER:
                return Character.class;
            case C_BYTE:
                return Byte.class;
            case C_SHORT:
                return Short.class;
            case C_INTEGER:
                return Integer.class;
            case C_LONG:
                return Long.class;
            case C_FLOAT:
                return Float.class;
            case C_DOUBLE:
                return Double.class;
            case C_BOOLEAN:
                return Boolean.class;
            case C_OBJECT:
                return Object.class;
            case C_P_BOOLEAN:
                return boolean.class;
            case C_P_BYTE:
                return byte.class;
            case C_P_CHAR:
                return char.class;
            case C_P_SHORT:
                return short.class;
            case C_P_INT:
                return int.class;
            case C_P_LONG:
                return long.class;
            case C_P_FLOAT:
                return float.class;
            case C_P_DOUBLE:
                return double.class;
            default:
                throw new MVStoreIndexException("Unknown array component id " + componentId);
        }
    }

    private static void writeString(final WriteBuffer buff, final String value) {
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        buff.putVarInt(bytes.length).put(bytes);
    }

    private static String readString(final ByteBuffer buff) {
        final int length = DataUtils.readVarInt(buff);
        final byte[] bytes = new byte[length];
        buff.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static int zigZag(final int value) {
        return (value << 1) ^ (value >> 31);
    }

    private static long zigZag(final long value) {
        return (value << 1) ^ (value >> 63);
    }

    private static int unZigZag(final int value) {
        return (value >>> 1) ^ -(value & 1);
    }

    private static long unZigZag(final long value) {
        return (value >>> 1) ^ -(value & 1L);
    }

    private static byte[] javaSerialize(final Object value) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (final ObjectOutputStream objectOut = new ObjectOutputStream(out)) {
                objectOut.writeObject(value);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new MVStoreIndexException("Failed to serialize a value of type " + value.getClass(), e);
        }
    }

    private static Object javaDeserialize(final byte[] bytes) {
        try (final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new MVStoreIndexException("Failed to deserialize a fallback value", e);
        }
    }
}
