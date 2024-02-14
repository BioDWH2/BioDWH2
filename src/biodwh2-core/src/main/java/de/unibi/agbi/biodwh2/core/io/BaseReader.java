package de.unibi.agbi.biodwh2.core.io;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseReader<T> implements Iterable<T>, AutoCloseable {
    protected final BufferedReader reader;
    protected T lastEntry;

    public BaseReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public BaseReader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
    }

    protected String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                if (lastEntry == null)
                    lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public T next() {
                final T entry = lastEntry;
                lastEntry = null;
                return entry;
            }
        };
    }

    protected abstract T readNextEntry();

    public T[] readAll() {
        final List<T> entries = new ArrayList<>();
        for (T entry : this)
            entries.add(entry);
        //noinspection unchecked
        return (T[]) entries.toArray();
    }

    @Override
    public void close() throws IOException {
        if (reader != null)
            reader.close();
    }
}
