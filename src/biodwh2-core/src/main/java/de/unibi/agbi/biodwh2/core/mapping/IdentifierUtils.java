package de.unibi.agbi.biodwh2.core.mapping;

import java.util.regex.Pattern;

public final class IdentifierUtils {
    private static final Pattern CAS_NUMBER_PATTERN = Pattern.compile("[0-9]{2,7}-[0-9]{2}-[0-9]");

    private IdentifierUtils() {
    }

    public static boolean isCasNumber(final String casNumber) {
        if (casNumber == null || !CAS_NUMBER_PATTERN.matcher(casNumber).matches())
            return false;
        final int casLength = casNumber.length();
        final int checkDigit = getDigitFromString(casNumber, casLength - 1);
        int checkSum = getDigitFromString(casNumber, casLength - 3) + getDigitFromString(casNumber, casLength - 4) * 2;
        for (int i = casLength - 6; i >= 0; i--)
            checkSum += getDigitFromString(casNumber, i) * (3 + casLength - 6 - i);
        return (checkSum % 10) == checkDigit;
    }

    private static int getDigitFromString(final String s, final int position) {
        return Integer.parseInt(s.substring(position, position + 1));
    }
}
