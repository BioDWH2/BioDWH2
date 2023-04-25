package de.unibi.agbi.biodwh2.core.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IdentifierUtils {
    /**
     * https://www.uniprot.org/help/accession_numbers
     */
    public static final Pattern UNIPROT_KB_PATTERN = Pattern.compile(
            "[OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z][0-9]([A-Z][A-Z0-9]{2}[0-9]){1,2}");
    public static final Pattern CAS_NUMBER_PATTERN = Pattern.compile("[0-9]{2,7}-[0-9]{2}-[0-9]");
    public static final Pattern DRUGBANK_DRUG_ID_PATTERN = Pattern.compile("DB[0-9]{5}");
    public static final Pattern CHEMBL_ID_PATTERN = Pattern.compile("CHEMBL[0-9]+");
    /**
     * https://www.crossref.org/blog/dois-and-matching-regular-expressions/
     */
    public static final Pattern DOI_PATTERN = Pattern.compile("10.\\d{4,9}/[-._;()/:A-Z0-9]+",
                                                              Pattern.CASE_INSENSITIVE);

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

    public static String[] extractDOIs(final String s) {
        if (s == null)
            return null;
        final Matcher matcher = DOI_PATTERN.matcher(s);
        final List<String> dois = new ArrayList<>();
        while (matcher.find())
            dois.add(matcher.group(0));
        return dois.size() > 0 ? dois.toArray(new String[0]) : null;
    }
}
