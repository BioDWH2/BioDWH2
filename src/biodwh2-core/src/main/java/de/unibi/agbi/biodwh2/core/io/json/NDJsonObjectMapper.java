package de.unibi.agbi.biodwh2.core.io.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;

public final class NDJsonObjectMapper {
    private final ObjectMapper mapper;

    public NDJsonObjectMapper() {
        mapper = new ObjectMapper();
    }

    public <T> Iterator<T> readValues(final String content, final Class<T> valueType) {
        return Arrays.stream(StringUtils.split(content, '\n')).map(String::trim).filter(l -> l.length() > 0).map(
                l -> lineToJson(l, valueType)).iterator();
    }

    private <T> T lineToJson(final String line, final Class<T> valueType) {
        try {
            return mapper.readValue(line, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public <T> Iterator<T> readValues(final InputStream stream, final Class<T> valueType) {
        return readValues(FileUtils.createBufferedReaderFromStream(stream), valueType);
    }

    public <T> Iterator<T> readValues(final Reader reader, final Class<T> valueType) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        return new Iterator<T>() {
            private String lastLine = null;

            @Override
            public boolean hasNext() {
                lastLine = readNextNonEmptyLineSafe(bufferedReader);
                return lastLine != null;
            }

            @Override
            public T next() {
                return lastLine != null ? lineToJson(lastLine, valueType) : null;
            }
        };
    }

    private String readNextNonEmptyLineSafe(final BufferedReader reader) {
        String line;
        while ((line = readLineSafe(reader)) != null) {
            line = line.trim();
            if (line.length() > 0)
                break;
        }
        return line;
    }

    private String readLineSafe(final BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException ignored) {
            return null;
        }
    }
}
