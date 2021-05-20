package de.unibi.agbi.biodwh2.core.mapping;

public final class CitationUtils {
    private CitationUtils() {
    }

    public static String getAMACitation(final String authors, final String title, final String volume,
                                        final String year, final String journal, final String pages, final String issue,
                                        final String doi) {
        final StringBuilder builder = new StringBuilder();
        if (authors != null)
            builder.append(authors);
        builder.append(". ").append(title).append(' ').append(journal).append(". ").append(year);
        if (volume != null || issue != null || pages != null) {
            builder.append(';');
            if (volume != null)
                builder.append(volume);
            if (issue != null)
                builder.append('(').append(issue).append(')');
            if (pages != null)
                builder.append(':').append(pages);
            builder.append('.');
        }
        if (doi != null)
            builder.append(" doi:").append(doi).append('.');
        return builder.toString();
    }
}
