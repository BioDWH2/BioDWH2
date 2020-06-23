package de.unibi.agbi.biodwh2.core.io;

import de.unibi.agbi.biodwh2.core.exceptions.GraphCacheException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public final class ValuePacker {
    private static final String PackedStringArraySplitter = "','";
    private static final String UnescapedQuotes = "'";
    private static final String EscapedQuotes = "''";
    private static final char UnescapedComma = ',';

    private static final Map<Class<?>, String> TypePackedPrefixMap = new HashMap<>();
    private static final Map<String, Class<?>> PackedPrefixTypeMap = new HashMap<>();

    static {
        TypePackedPrefixMap.put(String.class, "S|");
        TypePackedPrefixMap.put(int.class, "I|");
        TypePackedPrefixMap.put(Integer.class, "I|");
        TypePackedPrefixMap.put(long.class, "L|");
        TypePackedPrefixMap.put(Long.class, "L|");
        TypePackedPrefixMap.put(boolean.class, "Bo|");
        TypePackedPrefixMap.put(Boolean.class, "Bo|");
        TypePackedPrefixMap.put(byte.class, "B|");
        TypePackedPrefixMap.put(Byte.class, "B|");
        TypePackedPrefixMap.put(String[].class, "S[]|");
        TypePackedPrefixMap.put(int[].class, "I[]|");
        TypePackedPrefixMap.put(Integer[].class, "I[]|");
        TypePackedPrefixMap.put(long[].class, "L[]|");
        TypePackedPrefixMap.put(Long[].class, "L[]|");
        TypePackedPrefixMap.put(boolean[].class, "Bo[]|");
        TypePackedPrefixMap.put(Boolean[].class, "Bo[]|");
        TypePackedPrefixMap.put(byte[].class, "B[]|");
        TypePackedPrefixMap.put(Byte[].class, "B[]|");
        PackedPrefixTypeMap.put("S|", String.class);
        PackedPrefixTypeMap.put("I|", Integer.class);
        PackedPrefixTypeMap.put("L|", Long.class);
        PackedPrefixTypeMap.put("Bo|", Boolean.class);
        PackedPrefixTypeMap.put("B|", Byte.class);
        PackedPrefixTypeMap.put("S[]|", String[].class);
        PackedPrefixTypeMap.put("I[]|", Integer[].class);
        PackedPrefixTypeMap.put("L[]|", Long[].class);
        PackedPrefixTypeMap.put("Bo[]|", Boolean[].class);
        PackedPrefixTypeMap.put("B[]|", Byte[].class);
    }

    private ValuePacker() {
    }

    public static String packValue(Object value) {
        final String packedPrefix = getTypePackedPrefix(value.getClass());
        if (!value.getClass().isArray())
            return packedPrefix + value;
        if (value instanceof int[])
            return packedPrefix + StringUtils.join((int[]) value, UnescapedComma);
        if (value instanceof long[])
            return packedPrefix + StringUtils.join((long[]) value, UnescapedComma);
        if (value instanceof byte[])
            return packedPrefix + StringUtils.join((byte[]) value, UnescapedComma);
        StringBuilder joinedArray = new StringBuilder();
        if (value instanceof boolean[]) {
            boolean[] array = (boolean[]) value;
            for (int i = 0; i < array.length; i++) {
                if (i > 0)
                    joinedArray.append(UnescapedComma);
                joinedArray.append(array[i]);
            }
            return packedPrefix + joinedArray;
        }
        Object[] array = (Object[]) value;
        if (array.length == 0)
            return packedPrefix;
        if (array[0] instanceof CharSequence) {
            joinedArray.append(UnescapedQuotes).append(array[0].toString().replace(UnescapedQuotes, EscapedQuotes));
            for (int i = 1; i < array.length; i++) {
                joinedArray.append(PackedStringArraySplitter);
                joinedArray.append(array[i].toString().replace(UnescapedQuotes, EscapedQuotes));
            }
            joinedArray.append(UnescapedQuotes);
        } else {
            joinedArray.append(array[0].toString());
            for (int i = 1; i < array.length; i++)
                joinedArray.append(UnescapedComma).append(array[i].toString());
        }
        return packedPrefix + joinedArray;
    }

    private static String getTypePackedPrefix(Class<?> type) {
        if (!TypePackedPrefixMap.containsKey(type)) {
            String prefix = type.isArray() ? type.getComponentType().getName() + "[]|" : type.getName() + "|";
            TypePackedPrefixMap.put(type, prefix);
            PackedPrefixTypeMap.put(prefix, type);
        }
        return TypePackedPrefixMap.get(type);
    }

    public static Object unpackValue(String packedValue) throws GraphCacheException {
        int prefixEndIndex = packedValue.indexOf("|") + 1;
        Class<?> type;
        try {
            type = getPackedPrefixType(packedValue.substring(0, prefixEndIndex));
        } catch (ClassNotFoundException e) {
            throw new GraphCacheException("Failed to persist graph", e);
        }
        String value = packedValue.substring(prefixEndIndex);
        if (type == String.class)
            return value;
        if (type == Integer.class)
            return Integer.parseInt(value);
        if (type == Long.class)
            return Long.parseLong(value);
        if (type == Boolean.class)
            return Boolean.parseBoolean(value);
        if (type == Byte.class)
            return Byte.parseByte(value);
        if (type.isArray()) {
            Class<?> valueType = type.getComponentType();
            if (value.length() == 0)
                return java.lang.reflect.Array.newInstance(valueType, 0);
            if (valueType == String.class) {
                value = value.substring(1, value.length() - 1).replace(EscapedQuotes, UnescapedQuotes);
                return StringUtils.splitByWholeSeparator(value, PackedStringArraySplitter);
            }
            String[] parts = StringUtils.split(value, UnescapedComma);
            Object[] array = (Object[]) java.lang.reflect.Array.newInstance(valueType, parts.length);
            if (valueType == Integer.class)
                for (int i = 0; i < parts.length; i++)
                    array[i] = Integer.parseInt(parts[i]);
            else if (valueType == Long.class)
                for (int i = 0; i < parts.length; i++)
                    array[i] = Long.parseLong(parts[i]);
            else if (valueType == Boolean.class)
                for (int i = 0; i < parts.length; i++)
                    array[i] = Boolean.parseBoolean(parts[i]);
            else if (valueType == Byte.class)
                for (int i = 0; i < parts.length; i++)
                    array[i] = Byte.parseByte(parts[i]);
            return array;
        }
        return null;
    }

    private static Class<?> getPackedPrefixType(String prefix) throws ClassNotFoundException {
        if (!PackedPrefixTypeMap.containsKey(prefix)) {
            String typeName = prefix.substring(0, prefix.length() - 1);
            Class<?> type = typeName.endsWith("[]") ? Class.forName(
                    "[L" + typeName.substring(0, typeName.length() - 2) + ";") : Class.forName(typeName);
            TypePackedPrefixMap.put(type, prefix);
            PackedPrefixTypeMap.put(prefix, type);
        }
        return PackedPrefixTypeMap.get(prefix);
    }

    public static boolean isValuePackable(Object value) {
        Class<?> valueType = value.getClass();
        if (valueType.isArray())
            valueType = valueType.getComponentType();
        return ClassUtils.isPrimitiveOrWrapper(valueType) || valueType == String.class;
    }
}
