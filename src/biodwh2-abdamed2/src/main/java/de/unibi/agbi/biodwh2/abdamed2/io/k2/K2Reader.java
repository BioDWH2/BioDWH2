package de.unibi.agbi.biodwh2.abdamed2.io.k2;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public final class K2Reader implements Iterable<K2Entry> {
    private final BufferedReader reader;
    private final K2KEntry header;
    private final Map<String, K2FEntry> fields;
    K2Entry lastEntry;
    private String nextEntryType;

    @SuppressWarnings("unused")
    public K2Reader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public K2Reader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
        fields = new HashMap<>();
        final K2Entry entry = readNextEntry();
        if (entry == null || entry.getType() != EntryType.K) {
            throw new K2FormatException("K2 file needs to start with a 'K' block");
        }
        header = (K2KEntry) entry;
        for (int i = 0; i < header.getNumberOfFBlocks(); i++) {
            final K2Entry fieldEntry = readNextEntry();
            if (fieldEntry == null || fieldEntry.getType() != EntryType.F) {
                throw new K2FormatException("K2 file has less 'F' blocks than advertised by the header");
            }
            final K2FEntry field = (K2FEntry) fieldEntry;
            fields.put(field.getIdentifier(), field);
        }
    }

    @Override
    public Iterator<K2Entry> iterator() {
        return new Iterator<K2Entry>() {
            @Override
            public boolean hasNext() {
                if (lastEntry != null && lastEntry.getType() == EntryType.E)
                    return false;
                lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public K2Entry next() {
                return lastEntry;
            }
        };
    }

    K2Entry readNextEntry() {
        if (nextEntryType != null) {
            final String tag = nextEntryType;
            nextEntryType = null;
            final K2Entry entry = K2Entry.fromType(tag);
            readEntryContents(entry);
            return entry;
        }
        String line;
        while ((line = readLineSafe()) != null) {
            if (line.trim().length() <= 0)
                continue;
            if (StringUtils.startsWith(line, "00")) {
                final K2Entry entry = K2Entry.fromType(line.substring(2).trim());
                readEntryContents(entry);
                return entry;
            }
        }
        return null;
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
            return null;
        }
    }

    private void readEntryContents(final K2Entry entry) {
        final List<String> lines = new ArrayList<>();
        final List<K2FEntry> entryFields = new ArrayList<>();
        String seekLine;
        while ((seekLine = readLineSafe()) != null) {
            if (StringUtils.startsWith(seekLine, "00")) {
                nextEntryType = seekLine.substring(2).trim();
                break;
            } else if (seekLine.length() > 0) {
                final String identifier = seekLine.substring(0, 2);
                if (entry.getType() != EntryType.K && entry.getType() != EntryType.F && entry.getType() != EntryType.E)
                    entryFields.add(fields.get(identifier));
                final String value = seekLine.substring(2).trim();
                lines.add(value.length() > 0 ? value : null);
            }
        }
        entry.parse(lines.toArray(new String[0]), entryFields);
    }

    public K2KEntry getHeader() {
        return header;
    }

    public K2FEntry[] getFields() {
        return fields.values().toArray(new K2FEntry[0]);
    }
}
