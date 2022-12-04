package de.unibi.agbi.biodwh2.ttd.etl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FlatFileWithoutIDTTDReader implements Iterable<FlatFileTTDEntry>, AutoCloseable {
    private final BufferedReader reader;
    private FlatFileTTDEntry lastEntry;
    private int dotLine = 0;

    public FlatFileWithoutIDTTDReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public FlatFileWithoutIDTTDReader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
    }

    private void addKeyValuePair(final Map<String, List<String>> entry, final String key, final String value) {
        if (!entry.containsKey(key))
            entry.put(key, new ArrayList<>());
        entry.get(key).add(value);
    }

    private FlatFileTTDEntry readNextEntry() {
        final FlatFileTTDEntry entry = new FlatFileTTDEntry();
        String line;

        while ((line = readLineSafe()) != null) {
            if (StringUtils.isNotBlank(line)) {
                final String[] parts = StringUtils.split(line, "\t", 2);
                if (line.startsWith("-------"))
                    dotLine += 1;
                if (parts.length != 2 || dotLine < 2)
                    continue;
                addKeyValuePair(entry.properties, parts[0].trim(), parts[1].trim());
            } else {
                return entry;
            }
        }

        return entry.properties.size() > 0 ? entry : null;
    }


    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
        }
        return null;
    }


    @Override
    public Iterator<FlatFileTTDEntry> iterator() {
        return new Iterator<FlatFileTTDEntry>() {
            @Override
            public boolean hasNext() {
                lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public FlatFileTTDEntry next() {
                return lastEntry;
            }
        };
    }

    @Override
    public void close() throws IOException {
        if (reader != null)
            reader.close();
    }
}

