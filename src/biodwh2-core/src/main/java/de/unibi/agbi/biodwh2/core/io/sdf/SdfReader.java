package de.unibi.agbi.biodwh2.core.io.sdf;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Iterator;

public final class SdfReader implements Iterable<SdfEntry> {
    private BufferedReader reader;
    private SdfEntry lastEntry;

    @SuppressWarnings("unused")
    public SdfReader(final String filePath, final String charsetName) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charsetName);
    }

    public SdfReader(final InputStream stream, final String charsetName) throws UnsupportedEncodingException {
        InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charsetName));
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

    private SdfEntry readNextEntry() {
        SdfEntry entry = new SdfEntry();
        entry.title = readLineSafe();
        if (entry.title == null)
            return null;
        entry.programTimestamp = readLineSafe();
        if (entry.programTimestamp == null)
            return null;
        entry.comment = readLineSafe();
        if (entry.comment == null)
            return null;
        String line;
        boolean inConnectionTable = true;
        String lastPropertyKey = null;
        StringBuilder connectionTable = new StringBuilder();
        while ((line = readLineSafe()) != null) {
            if (inConnectionTable) {
                if (line.trim().equals("M  END")) {
                    inConnectionTable = false;
                    entry.connectionTable = connectionTable.toString();
                } else {
                    if (connectionTable.length() > 0)
                        connectionTable.append("\n");
                    connectionTable.append(line);
                }
            } else if (line.trim().equals("$$$$")) {
                trimSdfPropertiesCarriageReturn(entry);
                return entry;
            } else {
                if (line.startsWith("> ")) {
                    lastPropertyKey = line.substring(line.indexOf("<") + 1, line.length() - 1);
                    entry.properties.put(lastPropertyKey, "");
                } else if (lastPropertyKey != null) {
                    entry.properties.put(lastPropertyKey, entry.properties.get(lastPropertyKey) + "\n" + line);
                }
            }
        }
        return null;
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (Exception ignored) {
        }
        return null;
    }

    private static void trimSdfPropertiesCarriageReturn(final SdfEntry entry) {
        entry.properties.replaceAll((k, v) -> entry.properties.get(k).trim());
    }
}
