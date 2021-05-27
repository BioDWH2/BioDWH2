package de.unibi.agbi.biodwh2.core.io.obo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OBO file format 1.4 reader
 * <p/>
 * http://purl.obolibrary.org/obo/oboformat/spec.html
 */
public final class OboReader implements Iterable<OboEntry> {
    private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile("\"(\\.|[^\"])*\"");

    private final BufferedReader reader;
    private final OboHeader header;
    OboEntry lastEntry;
    boolean hasNextEntry;
    private String nextType;

    public OboReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public OboReader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
        header = (OboHeader) readNextEntry();
    }

    OboStructure readNextEntry() {
        final OboStructure entry = instantiateEntryFromType();
        nextType = null;
        hasNextEntry = false;
        String line;
        while ((line = readLineSafe()) != null) {
            if (StringUtils.isNotBlank(line) && line.charAt(0) != '!') {
                if (line.charAt(0) == '[') {
                    nextType = StringUtils.strip(line, "[]");
                    hasNextEntry = true;
                    break;
                }
                final String[] parts = StringUtils.split(line, ":", 2);
                entry.addKeyValuePair(parts[0].trim(), removeComments(parts[1]).trim());
            }
        }
        return entry;
    }

    private OboStructure instantiateEntryFromType() {
        if (nextType == null)
            return new OboHeader();
        switch (nextType) {
            case "Term":
                return new OboTerm();
            case "Typedef":
                return new OboTypedef();
            case "Instance":
                return new OboInstance();
            default:
                return new OboEntry(nextType);
        }
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
        }
        return null;
    }

    private String removeComments(String value) {
        int commentIndex = StringUtils.indexOfIgnoreCase(value, "!");
        if (commentIndex == -1)
            return value;
        final Matcher matcher = QUOTED_STRING_PATTERN.matcher(value);
        while (matcher.find() && commentIndex != -1) {
            if (commentIndex < matcher.start())
                return value.substring(0, commentIndex);
            commentIndex = StringUtils.indexOfIgnoreCase(value, "!", matcher.end());
        }
        return commentIndex == -1 ? value : value.substring(0, commentIndex);
    }

    public OboHeader getHeader() {
        return header;
    }

    @Override
    public Iterator<OboEntry> iterator() {
        return new Iterator<OboEntry>() {
            @Override
            public boolean hasNext() {
                final boolean lastHasNextEntry = hasNextEntry;
                if (lastHasNextEntry)
                    lastEntry = (OboEntry) readNextEntry();
                return lastHasNextEntry;
            }

            @Override
            public OboEntry next() {
                return lastEntry;
            }
        };
    }
}
