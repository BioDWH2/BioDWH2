package de.unibi.agbi.biodwh2.drugmap.etl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class FlatFileDrugMapReader implements Iterable<FlatFileDrugMapEntry>, AutoCloseable {
    private final BufferedReader reader;
    private FlatFileDrugMapEntry lastEntry;
    private FlatFileDrugMapEntry currentEntry = new FlatFileDrugMapEntry();

    @SuppressWarnings("unused")
    public FlatFileDrugMapReader(final String filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(new File(filePath)), charset);
    }

    public FlatFileDrugMapReader(final Path filePath, final Charset charset) throws IOException {
        this(FileUtils.openInputStream(filePath.toFile()), charset);
    }

    public FlatFileDrugMapReader(final InputStream stream, final Charset charset) {
        final InputStream baseStream = new BufferedInputStream(stream);
        reader = new BufferedReader(new InputStreamReader(baseStream, charset));
    }

    private void addKeyValuePair(final Map<String, List<String>> entry, final String key, final String value) {
        if (!entry.containsKey(key))
            entry.put(key, new ArrayList<>());
        entry.get(key).add(value);
    }

    private FlatFileDrugMapEntry readNextEntry() {
        final FlatFileDrugMapEntry entry = currentEntry;
        String currentIdentifier = entry.getID();
        String line;
        boolean hasNext = false;
        while ((line = readLineSafe()) != null) {
            if (StringUtils.isNotBlank(line)) {
                final String[] parts = StringUtils.split(line, "\t", 3);
                if (parts.length != 3)
                    continue;
                String identifier = parts[0].trim();
                if (currentIdentifier == null || identifier.equals(currentIdentifier)) {
                    if (currentIdentifier == null)
                        currentIdentifier = identifier;
                    addKeyValuePair(entry.properties, parts[1].trim(), parts[2].trim());
                    entry.setID(identifier);
                } else {
                    currentEntry = new FlatFileDrugMapEntry();
                    addKeyValuePair(currentEntry.properties, parts[1].trim(), parts[2].trim());
                    currentEntry.setID(identifier);
                    hasNext = true;
                    break;
                }
            }
        }
        if (!hasNext)
            currentEntry = new FlatFileDrugMapEntry();
        return !entry.properties.isEmpty() ? entry : null;
    }

    private String readLineSafe() {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public Iterator<FlatFileDrugMapEntry> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                lastEntry = readNextEntry();
                return lastEntry != null;
            }

            @Override
            public FlatFileDrugMapEntry next() {
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
