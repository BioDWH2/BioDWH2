package de.unibi.agbi.biodwh2.core.io.fasta;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public final class FastaReader implements Iterable<FastaEntry>, AutoCloseable {
    private final BufferedReader reader;
    FastaEntry lastEntry;
    private String nextHeader;

    @SuppressWarnings("unused")
    public FastaReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public FastaReader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
    }

    @Override
    public Iterator<FastaEntry> iterator() {
        return new Iterator<FastaEntry>() {
            @Override
            public boolean hasNext() {
                if (lastEntry == null)
                    lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public FastaEntry next() {
                final FastaEntry entry = lastEntry;
                lastEntry = null;
                return entry;
            }
        };
    }

    FastaEntry readNextEntry() {
        if (nextHeader == null)
            nextHeader = readLineSafe();
        if (nextHeader == null)
            return null;
        final FastaEntry newEntry = new FastaEntry();
        newEntry.setHeader(nextHeader);
        final StringBuilder sequence = new StringBuilder();
        String line;
        while ((line = readLineSafe()) != null) {
            if (line.startsWith(">")) {
                break;
            }
            if (StringUtils.isNotBlank(line))
                sequence.append(line.trim());
        }
        nextHeader = line;
        newEntry.setSequence(sequence.toString());
        return newEntry;
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        if (reader != null)
            reader.close();
    }
}
