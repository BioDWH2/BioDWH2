package de.unibi.agbi.biodwh2.mirbase.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public final class AlignmentUtils {
    public static String getFoldStringFromHairpinAlignment(final String alignment) {
        final String[] lines = StringUtils.split(alignment, '\n');
        if (lines.length != 5)
            return null;
        int maxLength = 0;
        // First convert all to lowercase and find out the max length
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].toLowerCase(Locale.ROOT);
            maxLength = Math.max(maxLength, lines[i].length());
        }
        // Append spaces if a line got trimmed
        for (int i = 0; i < lines.length; i++)
            while (lines[i].length() < maxLength)
                lines[i] += ' ';
        final StringBuilder result = new StringBuilder();
        // Upper lines
        for (int i = 0; i < maxLength; i++) {
            final char outerChar = lines[0].charAt(i);
            if (outerChar == '-')
                continue;
            if (outerChar == 'a' || outerChar == 'c' || outerChar == 'g' || outerChar == 'u') {
                result.append('.');
                continue;
            }
            final char innerChar = lines[1].charAt(i);
            final char alignmentChar = lines[2].charAt(i);
            if (innerChar == 'a' || innerChar == 'c' || innerChar == 'g' || innerChar == 'u')
                result.append(alignmentChar == '|' ? '(' : '.');
        }
        // Lower lines
        for (int i = maxLength - 1; i >= 0; i--) {
            final char outerChar = lines[4].charAt(i);
            if (outerChar == '-')
                continue;
            if (outerChar == 'a' || outerChar == 'c' || outerChar == 'g' || outerChar == 'u') {
                result.append('.');
                continue;
            }
            final char innerChar = lines[3].charAt(i);
            final char alignmentChar = lines[2].charAt(i);
            if (innerChar == 'a' || innerChar == 'c' || innerChar == 'g' || innerChar == 'u')
                result.append(alignmentChar == '|' ? ')' : '.');
        }
        return result.toString();
    }
}
