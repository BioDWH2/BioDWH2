package de.unibi.agbi.biodwh2.core.io.sdf;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public final class SdfReader implements Iterable<SdfEntry> {
    private BufferedReader reader;
    SdfEntry lastEntry;

    @SuppressWarnings("unused")
    public SdfReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public SdfReader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
    }

    @Override
    public Iterator<SdfEntry> iterator() {
        return new Iterator<SdfEntry>() {
            @Override
            public boolean hasNext() {
                lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public SdfEntry next() {
                return lastEntry;
            }
        };
    }

    SdfEntry readNextEntry() {
        final SdfEntry entry = readNextEntryHeader();
        if (entry == null)
            return null;
        boolean inConnectionTable = true;
        String lastPropertyKey = null;
        final StringBuilder connectionTable = new StringBuilder();
        String line = readLineSafe();
        while (line != null) {
            if (inConnectionTable) {
                if (line.trim().equals("M  END")) {
                    inConnectionTable = false;
                    entry.setConnectionTable(connectionTable.toString());
                } else {
                    if (connectionTable.length() > 0)
                        connectionTable.append('\n');
                    connectionTable.append(line);
                }
            } else if (line.trim().equals("$$$$")) {
                trimSdfPropertiesCarriageReturn(entry);
                return entry;
            } else {
                if (line.startsWith("> ")) {
                    lastPropertyKey = line.substring(line.indexOf('<') + 1, line.length() - 1);
                    entry.properties.put(lastPropertyKey, "");
                } else if (lastPropertyKey != null) {
                    entry.properties.put(lastPropertyKey, entry.properties.get(lastPropertyKey) + "\n" + line);
                }
            }
            line = readLineSafe();
        }
        return null;
    }

    private SdfEntry readNextEntryHeader() {
        final SdfEntry entry = new SdfEntry();
        entry.setTitle(readLineSafe());
        entry.setProgramTimestamp(readLineSafe());
        entry.setComment(readLineSafe());
        return readingNextEntryHeaderFailed(entry) ? null : entry;
    }

    private boolean readingNextEntryHeaderFailed(final SdfEntry entry) {
        return entry.getTitle() == null || entry.getProgramTimestamp() == null || entry.getComment() == null;
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
            return null;
        }
    }

    private static void trimSdfPropertiesCarriageReturn(final SdfEntry entry) {
        entry.properties.replaceAll((k, v) -> entry.properties.get(k).trim());
    }
}
