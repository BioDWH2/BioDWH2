package de.unibi.agbi.biodwh2.core.io.sdf;

import de.unibi.agbi.biodwh2.core.io.BaseReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class SdfReader extends BaseReader<SdfEntry> {
    @SuppressWarnings("unused")
    public SdfReader(final String filePath, final Charset charset) throws IOException {
        super(filePath, charset);
    }

    public SdfReader(final InputStream stream, final Charset charset) {
        super(stream, charset);
    }

    @Override
    protected SdfEntry readNextEntry() {
        final SdfEntry entry = readNextEntryHeader();
        if (entry == null)
            return null;
        boolean inConnectionTable = true;
        String lastPropertyKey = null;
        final StringBuilder connectionTable = new StringBuilder();
        String line = readLineSafe();
        while (line != null) {
            if (inConnectionTable) {
                if ("M  END".equals(line.trim())) {
                    inConnectionTable = false;
                    entry.setConnectionTable(connectionTable.toString());
                } else {
                    if (connectionTable.length() > 0)
                        connectionTable.append('\n');
                    connectionTable.append(line);
                }
            } else if ("$$$$".equals(line.trim())) {
                trimSdfPropertiesCarriageReturn(entry);
                return entry;
            } else {
                if (line.startsWith("> ")) {
                    lastPropertyKey = line.substring(line.indexOf('<') + 1, line.length() - 1);
                    entry.properties.put(lastPropertyKey, "");
                } else if (lastPropertyKey != null) {
                    entry.properties.put(lastPropertyKey, entry.properties.get(lastPropertyKey) + '\n' + line);
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

    private static void trimSdfPropertiesCarriageReturn(final SdfEntry entry) {
        entry.properties.replaceAll((k, v) -> entry.properties.get(k).trim());
    }
}
