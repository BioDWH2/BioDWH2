package de.unibi.agbi.biodwh2.core.io.graph;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Locale;

public final class GraphMLPropertyFormatter {
    public static class PropertyType {
        public String typeName;
        public String listTypeName;
    }

    private static final String INVALID_XML_CHARS = new String(
            new char[]{0x01, 0x02, 0x03, 0x04, 0x08, 0x1d, 0x12, 0x14, 0x18});
    private static final String FORMAT = "%s";
    private static final String ARRAY_START = "[";
    private static final String ARRAY_END = "]";
    private static final String ARRAY_SEPARATOR = ",";
    private static final String STRING_ARRAY_SEPARATOR = "\",\"";

    private GraphMLPropertyFormatter() {
    }

    public static PropertyType getPropertyType(final Class<?> type) {
        final PropertyType p = new PropertyType();
        // Allowed types: boolean, int, long, float, double, string
        if (type.isArray()) {
            p.listTypeName = getTypeName(type.getComponentType());
            p.typeName = "string";
        } else if (Collection.class.isAssignableFrom(type)) {
            ParameterizedType superclass = (ParameterizedType) type.getGenericSuperclass();
            p.listTypeName = "string"; // TODO
            p.typeName = "string";
        } else
            p.typeName = getTypeName(type);
        return p;
    }

    private static String getTypeName(final Class<?> type) {
        if (CharSequence.class.isAssignableFrom(type) || type.equals(Character.class) || type.equals(char.class))
            return "string";
        final String typeName = type.getSimpleName().toLowerCase(Locale.US);
        return typeName.replace("integer", "int");
    }

    public static String format(final Object value) {
        if (value.getClass().isArray()) {
            final Class<?> componentType = value.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                if (componentType.equals(boolean.class))
                    return format((boolean[]) value);
                if (componentType.equals(byte.class))
                    return format((byte[]) value);
                if (componentType.equals(short.class))
                    return format((short[]) value);
                if (componentType.equals(int.class))
                    return format((int[]) value);
                if (componentType.equals(long.class))
                    return format((long[]) value);
                if (componentType.equals(float.class))
                    return format((float[]) value);
                if (componentType.equals(double.class))
                    return format((double[]) value);
                if (componentType.equals(char.class))
                    return format((char[]) value);
            }
            return format((Object[]) value);
        }
        if (value instanceof Collection)
            return format(((Collection<?>) value).toArray());
        return replaceInvalidXmlCharacters(String.format(Locale.US, FORMAT, value), false);
    }

    private static String replaceInvalidXmlCharacters(final CharSequence s, final boolean escapeQuotes) {
        final String cleanString = StringUtils.replaceChars(s.toString(), INVALID_XML_CHARS, "");
        return escapeQuotes ? StringUtils.replace(cleanString, "\"", "\\\"") : cleanString;
    }

    public static String format(final boolean[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append(array[0]);
        for (int i = 1; i < array.length; i++)
            builder.append(ARRAY_SEPARATOR).append(array[i]);
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final byte[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append(array[0]);
        for (int i = 1; i < array.length; i++)
            builder.append(ARRAY_SEPARATOR).append(array[i]);
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final short[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append(array[0]);
        for (int i = 1; i < array.length; i++)
            builder.append(ARRAY_SEPARATOR).append(array[i]);
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final int[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append(array[0]);
        for (int i = 1; i < array.length; i++)
            builder.append(ARRAY_SEPARATOR).append(array[i]);
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final long[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append(array[0]);
        for (int i = 1; i < array.length; i++)
            builder.append(ARRAY_SEPARATOR).append(array[i]);
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final float[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append(formatString(array[0]));
        for (int i = 1; i < array.length; i++)
            builder.append(ARRAY_SEPARATOR).append(formatString(array[i]));
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final double[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append(formatString(array[0]));
        for (int i = 1; i < array.length; i++)
            builder.append(ARRAY_SEPARATOR).append(formatString(array[i]));
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final char[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append("\"").append(array[0]);
        for (int i = 1; i < array.length; i++)
            builder.append(STRING_ARRAY_SEPARATOR).append(array[i]);
        if (array.length > 0)
            builder.append("\"");
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final CharSequence[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append("\"").append(replaceInvalidXmlCharacters(array[0], true));
        for (int i = 1; i < array.length; i++)
            builder.append(STRING_ARRAY_SEPARATOR).append(replaceInvalidXmlCharacters(array[i], true));
        if (array.length > 0)
            builder.append("\"");
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final Object[] array) {
        final Class<?> componentType = array.getClass().getComponentType();
        if (componentType.equals(CharSequence.class))
            return format((CharSequence[]) array);
        // If type erasure removed CharSequence or Character as the component type, check the elements directly
        if (array.length > 0) {
            for (Object o : array) {
                if (o instanceof CharSequence)
                    return format(convertToStringArray(array));
                if (o instanceof Character)
                    return format(convertToCharArray(array));
            }
        }
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append(replaceInvalidXmlCharacters(formatString(array[0]), true));
        for (int i = 1; i < array.length; i++)
            builder.append(ARRAY_SEPARATOR).append(replaceInvalidXmlCharacters(formatString(array[i]), true));
        return builder.append(ARRAY_END).toString();
    }

    private static String[] convertToStringArray(final Object[] array) {
        final String[] stringArray = new String[array.length];
        for (int i = 0; i < array.length; i++)
            stringArray[i] = array[i] == null ? null : array[i].toString();
        return stringArray;
    }

    private static char[] convertToCharArray(final Object[] array) {
        final char[] charArray = new char[array.length];
        for (int i = 0; i < array.length; i++)
            charArray[i] = (char) array[i];
        return charArray;
    }

    private static String formatString(Object value) {
        return String.format(Locale.US, FORMAT, value);
    }
}
