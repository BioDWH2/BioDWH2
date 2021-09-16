package de.unibi.agbi.biodwh2.core.io.gmt;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class GMTReader implements Iterable<GeneSet>, AutoCloseable {
    private final BufferedReader reader;
    private GeneSet lastEntry;

    public GMTReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public GMTReader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
    }

    @Override
    public Iterator<GeneSet> iterator() {
        return new Iterator<GeneSet>() {
            @Override
            public boolean hasNext() {
                lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public GeneSet next() {
                return lastEntry;
            }
        };
    }

    GeneSet readNextEntry() {
        String line;
        while ((line = readLineSafe()) != null) {
            if (line.trim().length() <= 0)
                continue;
            final String[] parts = StringUtils.split(line, '\t');
            if (parts.length < 2)
                continue;
            final String[] genes = Arrays.stream(parts).skip(2).filter(StringUtils::isNotEmpty).toArray(String[]::new);
            return new GeneSet(parts[0], parts[1], genes);
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

    public GeneSet[] readAll() {
        final List<GeneSet> entries = new ArrayList<>();
        for (GeneSet geneSet : this)
            entries.add(geneSet);
        return entries.toArray(new GeneSet[0]);
    }

    @Override
    public void close() throws Exception {
        if (reader != null)
            reader.close();
    }
}
