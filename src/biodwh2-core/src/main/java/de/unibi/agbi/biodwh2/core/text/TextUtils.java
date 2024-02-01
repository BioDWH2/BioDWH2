package de.unibi.agbi.biodwh2.core.text;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public final class TextUtils {
    public static final String[] MONTH_NAMES = {
            "january", "february", "march", "april", "may", "june", "july", "august", "september", "october",
            "november", "december"
    };
    public static final String[] THREE_LETTER_MONTH_NAMES = {
            "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"
    };
    private static final Map<String, Integer> monthNameNumberMap = new HashMap<>();
    private static final Map<String, Integer> threeLetterMonthNameNumberMap = new HashMap<>();

    static {
        for (int i = 0; i < MONTH_NAMES.length; i++) {
            monthNameNumberMap.put(MONTH_NAMES[i], i + 1);
            threeLetterMonthNameNumberMap.put(THREE_LETTER_MONTH_NAMES[i], i + 1);
        }
    }

    private TextUtils() {
    }

    public static int monthNameToInt(String name) {
        name = name.toLowerCase();
        final Integer result = monthNameNumberMap.get(name);
        return result != null ? result : -1;
    }

    public static int threeLetterMonthNameToInt(String name) {
        name = name.toLowerCase();
        final Integer result = threeLetterMonthNameNumberMap.get(name);
        return result != null ? result : -1;
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
