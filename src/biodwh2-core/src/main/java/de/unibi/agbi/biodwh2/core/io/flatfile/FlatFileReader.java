package de.unibi.agbi.biodwh2.core.io.flatfile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FlatFileReader implements Iterable<FlatFileEntry>, AutoCloseable {
    private final BufferedReader reader;
    private FlatFileEntry lastEntry;

    @SuppressWarnings("unused")
    public FlatFileReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public FlatFileReader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
    }

    @Override
    public Iterator<FlatFileEntry> iterator() {
        return new Iterator<FlatFileEntry>() {
            @Override
            public boolean hasNext() {
                if (lastEntry == null)
                    lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public FlatFileEntry next() {
                final FlatFileEntry entry = lastEntry;
                lastEntry = null;
                return entry;
            }
        };
    }

    private FlatFileEntry readNextEntry() {
        final FlatFileEntry entry = new FlatFileEntry();
        final List<String> currentChunk = new ArrayList<>();
        String line;
        while ((line = readLineSafe()) != null) {
            if (line.startsWith("//")) {
                processChunk(entry, currentChunk);
                currentChunk.clear();
                return entry;
            }
            if (line.startsWith("XX")) {
                processChunk(entry, currentChunk);
                currentChunk.clear();
            } else {
                currentChunk.add(line);
            }
        }
        return null;
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
        }
        return null;
    }

    private void processChunk(final FlatFileEntry entry, final List<String> chunk) {
        final List<FlatFileEntry.KeyValuePair> result = new ArrayList<>();
        String currentTag = "";
        StringBuilder tagChunk = new StringBuilder();
        for (final String line : chunk) {
            final boolean isLineContinuation = line.startsWith(" ");
            final String[] parts = isLineContinuation ? new String[]{"", line.trim()} : StringUtils.split(line, " \t",
                                                                                                          2);
            final String tag = parts[0];
            if (!currentTag.equals(tag) && !isLineContinuation) {
                if (!"".equals(currentTag) && tagChunk.length() > 0)
                    result.add(new FlatFileEntry.KeyValuePair(currentTag, tagChunk.toString()));
                currentTag = tag;
                tagChunk = new StringBuilder();
            }
            if (parts.length > 1) {
                if (tagChunk.length() > 0)
                    tagChunk.append('\n');
                tagChunk.append(parts[1].trim());
            }
        }
        if (!"".equals(currentTag) && tagChunk.length() > 0)
            result.add(new FlatFileEntry.KeyValuePair(currentTag, tagChunk.toString()));
        entry.properties.add(result);
    }

    @Override
    public void close() throws Exception {
        if (reader != null)
            reader.close();
    }
}
