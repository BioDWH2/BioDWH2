package de.unibi.agbi.biodwh2.core.text;

import org.apache.commons.lang3.StringUtils;

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
}
