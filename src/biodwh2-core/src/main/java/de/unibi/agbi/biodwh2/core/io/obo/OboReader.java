package de.unibi.agbi.biodwh2.core.io.obo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://owlcollab.github.io/oboformat/doc/obo-syntax.html
 */
@SuppressWarnings("WeakerAccess")
public final class OboReader implements Iterable<OboEntry> {
    private static final Pattern QuotedStringPattern = Pattern.compile("\"(\\.|[^\"])*\"");

    private BufferedReader reader;
    private final OboEntry header;
    private OboEntry lastEntry;
    private boolean hasNextEntry;
    private String nextName;

    @SuppressWarnings("unused")
    public OboReader(final String filePath, final String charsetName) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charsetName);
    }

    public OboReader(final InputStream stream, final String charsetName) throws IOException {
        InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charsetName));
        header = readNextEntry();
    }

    private OboEntry readNextEntry() {
        OboEntry entry = new OboEntry(nextName);
        String line;
        nextName = null;
        hasNextEntry = false;
        while ((line = readLineSafe()) != null) {
            if (line.startsWith("[")) {
                nextName = StringUtils.strip(line, "[]");
                hasNextEntry = true;
                break;
            }
            if (line.trim().length() == 0 || line.startsWith("!"))
                continue;
            String[] parts = StringUtils.split(line, ":", 2);
            entry.addKeyValuePair(parts[0].trim(), removeComments(parts[1]).trim());
        }
        return entry;
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (Exception ignored) {
        }
        return null;
    }

    private String removeComments(String value) {
        int commentIndex = StringUtils.indexOfIgnoreCase(value, "!");
        if (commentIndex == -1)
            return value;
        Matcher matcher = QuotedStringPattern.matcher(value);
        while (matcher.find() && commentIndex != -1) {
            if (commentIndex < matcher.start())
                return value.substring(0, commentIndex);
            commentIndex = StringUtils.indexOfIgnoreCase(value, "!", matcher.end());
        }
        return commentIndex != -1 ? value.substring(0, commentIndex) : value;
    }

    public OboEntry getHeader() {
        return header;
    }

    @Override
    public Iterator<OboEntry> iterator() {
        return new Iterator<OboEntry>() {
            @Override
            public boolean hasNext() {
                boolean lastHasNextEntry = hasNextEntry;
                lastEntry = readNextEntry();
                return lastHasNextEntry;
            }

            @Override
            public OboEntry next() {
                return lastEntry;
            }
        };
    }
}
