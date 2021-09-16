package de.unibi.agbi.biodwh2.core.mapping;

import java.util.regex.Pattern;

public final class IdentifierUtils {
    public static final Pattern CAS_NUMBER_PATTERN = Pattern.compile("[0-9]{2,7}-[0-9]{2}-[0-9]");
    public static final Pattern DRUGBANK_DRUG_ID_PATTERN = Pattern.compile("DB[0-9]{5}");
    public static final Pattern CHEMBL_ID_PATTERN = Pattern.compile("CHEMBL[0-9]+");

    private IdentifierUtils() {
    }

    public static boolean isCasNumber(final String s) {
        if (s == null || !CAS_NUMBER_PATTERN.matcher(s).matches())
            return false;
        final int casLength = s.length();
        final int checkDigit = getDigitFromString(s, casLength - 1);
        int checkSum = getDigitFromString(s, casLength - 3) + getDigitFromString(s, casLength - 4) * 2;
        for (int i = casLength - 6; i >= 0; i--)
            checkSum += getDigitFromString(s, i) * (3 + casLength - 6 - i);
        return (checkSum % 10) == checkDigit;
    }

    private static int getDigitFromString(final String s, final int position) {
        return Integer.parseInt(s.substring(position, position + 1));
    }
}
