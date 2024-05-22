package de.unibi.agbi.biodwh2.core.mapping;

import java.util.regex.Pattern;

public final class OpenArchivesInitiative {
    /**
     * https://www.openarchives.org/OAI/2.0/guidelines-oai-identifier.htm
     */
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(
            "oai:([A-Za-z][A-Za-z0-9\\-]*(?:\\.[A-Za-z][A-Za-z0-9\\-]*)+):((?:[;/?:@&=+$,A-Za-z0-9\\-_.!~*'()]|%[0-9ABCDEF]{2})+)");

    public static boolean isValidIdentifier(final String id) {
        return id != null && IDENTIFIER_PATTERN.matcher(id).matches();
    }

    public static String buildIdentifier(final String namespace, final String localIdentifier) {
        return buildIdentifier(namespace, localIdentifier, false);
    }

    public static String buildIdentifier(final String namespace, final String localIdentifier, final boolean escape) {
        final var result = "oai:" + namespace + ":" + (escape ? escape(localIdentifier) : localIdentifier);
        return isValidIdentifier(result) ? result : null;
    }

    public static String escape(final String text) {
        if (text == null)
            return null;
        final var builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            final var c = text.charAt(i);
            if (c == '/')
                builder.append("%2F");
            else if (c == '?')
                builder.append("%3F");
            else if (c == '#')
                builder.append("%23");
            else if (c == '=')
                builder.append("%3D");
            else if (c == '&')
                builder.append("%26");
            else if (c == ':')
                builder.append("%3A");
            else if (c == ';')
                builder.append("%3B");
            else if (c == ' ')
                builder.append("%20");
            else if (c == '%')
                builder.append("%25");
            else if (c == '+')
                builder.append("%2B");
            else
                builder.append(c);
        }
        return builder.toString();
    }
}
