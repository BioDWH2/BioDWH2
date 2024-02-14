package de.unibi.agbi.biodwh2.core.io.flatfile;

import de.unibi.agbi.biodwh2.core.io.BaseReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class FlatFileReader extends BaseReader<FlatFileEntry> {
    @SuppressWarnings("unused")
    public FlatFileReader(final String filePath, final Charset charset) throws IOException {
        super(filePath, charset);
    }

    public FlatFileReader(final InputStream stream, final Charset charset) {
        super(stream, charset);
    }

    @Override
    protected FlatFileEntry readNextEntry() {
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
                if (!currentTag.isEmpty() && tagChunk.length() > 0)
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
        if (!currentTag.isEmpty() && tagChunk.length() > 0)
            result.add(new FlatFileEntry.KeyValuePair(currentTag, tagChunk.toString()));
        entry.properties.add(result);
    }
}
