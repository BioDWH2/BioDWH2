package de.unibi.agbi.biodwh2.core.io.graph;

import de.unibi.agbi.biodwh2.core.io.mvstore.MVStoreId;
import de.unibi.agbi.biodwh2.core.lang.Type;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Locale;

public final class GraphMLPropertyFormatter {
    private static final String INVALID_XML_CHARS = new String(new char[]{
            '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\u0008', '\u000b',
            '\u000c', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016',
            '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', '\ufffe', '\uffff'
    });
    private static final String FORMAT = "%s";
    private static final String ARRAY_START = "[";
    private static final String ARRAY_END = "]";
    private static final String ARRAY_SEPARATOR = ",";
    private static final String STRING_ARRAY_SEPARATOR = "\",\"";

    private GraphMLPropertyFormatter() {
    }

    public static PropertyType getPropertyType(final Type type) {
        final PropertyType p = new PropertyType();
        // Allowed types: boolean, int, long, float, double, string
        if (type.getComponentType() != null) {
            p.listTypeName = getTypeName(type.getComponentType());
            p.typeName = "string";
        } else if (type.getType().equals(MVStoreId.class))
            p.typeName = "long";
        else
            p.typeName = getTypeName(type.getType());
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
            builder.append('"').append(array[0]);
        for (int i = 1; i < array.length; i++)
            builder.append(STRING_ARRAY_SEPARATOR).append(array[i]);
        if (array.length > 0)
            builder.append('"');
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final CharSequence[] array) {
        final StringBuilder builder = new StringBuilder(ARRAY_START);
        if (array.length > 0)
            builder.append('"').append(replaceInvalidXmlCharacters(array[0], true));
        for (int i = 1; i < array.length; i++)
            builder.append(STRING_ARRAY_SEPARATOR).append(replaceInvalidXmlCharacters(array[i], true));
        if (array.length > 0)
            builder.append('"');
        return builder.append(ARRAY_END).toString();
    }

    public static String format(final Object[] array) {
        final Class<?> componentType = array.getClass().getComponentType();
        if (componentType.equals(CharSequence.class))
            return format((CharSequence[]) array);
        // If type erasure removed CharSequence or Character as the component type, check the elements directly
        for (final Object o : array) {
            if (o instanceof CharSequence)
                return format(convertToStringArray(array));
            if (o instanceof Character)
                return format(convertToCharArray(array));
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

    private static String formatString(final Object value) {
        return String.format(Locale.US, FORMAT, value);
    }

    public static class PropertyType {
        public String typeName;
        public String listTypeName;
    }
}
