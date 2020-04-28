package de.unibi.agbi.biodwh2.core.mapping;

import java.util.regex.Pattern;

public final class IdentifierUtils {
    private static final Pattern EnsemblGenePattern = Pattern.compile("ENSG[0-9]+");
    private static final Pattern CasNumberPattern = Pattern.compile("[0-9]{2,7}-[0-9]{2}-[0-9]");
    private static final Pattern DrugbankMetabolitePattern = Pattern.compile("DBMET[0-9]{5}");
    private static final Pattern DrugbankSaltPattern = Pattern.compile("DBSALT[0-9]{6}");
    private static final Pattern DrugbankPattern = Pattern.compile("DB[0-9]{5}");

    public static boolean isCasNumber(final String casNumber) {
        if (casNumber == null || !CasNumberPattern.matcher(casNumber).matches())
            return false;
        int casLength = casNumber.length();
        int checkDigit = getDigitFromString(casNumber, casLength - 1);
        int checkSum = getDigitFromString(casNumber, casLength - 3) + getDigitFromString(casNumber, casLength - 4) * 2;
        for (int i = casLength - 6; i >= 0; i--)
            checkSum += getDigitFromString(casNumber, i) * (3 + casLength - 6 - i);
        return (checkSum % 10) == checkDigit;
    }

    private static int getDigitFromString(final String s, final int position) {
        return Integer.parseInt(s.substring(position, position + 1));
    }
}
