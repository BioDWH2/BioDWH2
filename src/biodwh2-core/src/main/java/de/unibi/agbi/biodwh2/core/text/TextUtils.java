package de.unibi.agbi.biodwh2.core.text;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class TextUtils {
    private TextUtils() {
    }

    public static String toUpperCamelCase(final String str) {
        final String[] parts = StringUtils.split(str, "_ ");
        final StringBuilder result = new StringBuilder();
        for (final String part : parts)
            result.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
        return result.toString();
    }

    public static String getProgressText(final long current, final long total) {
        return getProgressText(current, total, 2);
    }

    public static String getProgressText(final long current, final long total, final int decimals) {
        final double percent = Math.pow(10.0, decimals) / total * current;
        return String.format(Locale.US, "%s/%s (%." + decimals + "f%%)", current, total, percent);
    }

    public static List<Integer> allIndicesOf(final String text, final String search) {
        int initialIndex = text.indexOf(search);
        final List<Integer> result = new ArrayList<>();
        while (initialIndex != -1) {
            result.add(initialIndex);
            initialIndex = text.indexOf(search, initialIndex + 1);
        }
        return result;
    }
}
