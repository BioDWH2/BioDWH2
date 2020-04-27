package de.unibi.agbi.biodwh2.core.io;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

public final class FileUtils {
    private FileUtils() {
    }

    public static BufferedInputStream open(final Workspace workspace, final DataSource dataSource,
                                           final String fileName) throws IOException {
        String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        return new BufferedInputStream(new FileInputStream(filePath));
    }

    public static GZIPInputStream openGzip(final Workspace workspace, final DataSource dataSource,
                                           final String fileName) throws IOException {
        return new GZIPInputStream(open(workspace, dataSource, fileName));
    }

    public static ZipInputStream openZip(final Workspace workspace, final DataSource dataSource,
                                         final String fileName) throws IOException {
        return new ZipInputStream(open(workspace, dataSource, fileName));
    }

    public static <T> MappingIterator<T> openCsv(final Workspace workspace, final DataSource dataSource,
                                                 final String fileName, final Class<T> typeClass) throws IOException {
        InputStream stream = open(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, ',');
    }

    public static <T> MappingIterator<T> openSeparatedValuesFile(final InputStream stream, final Class<T> typeClass,
                                                                  final char separator) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return getFormatReader(typeClass, separator).readValues(reader);
    }

    private static <T> ObjectReader getFormatReader(final Class<T> typeClass, final char separator) {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(typeClass).withColumnSeparator(separator).withNullValue("");
        return csvMapper.readerFor(typeClass).with(schema);
    }

    public static <T> MappingIterator<T> openGzipCsv(final Workspace workspace, final DataSource dataSource,
                                                     final String fileName,
                                                     final Class<T> typeClass) throws IOException {
        InputStream stream = openGzip(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, ',');
    }

    public static <T> MappingIterator<T> openTsv(final Workspace workspace, final DataSource dataSource,
                                                 final String fileName, final Class<T> typeClass) throws IOException {
        InputStream stream = open(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, '\t');
    }

    public static <T> MappingIterator<T> openTsvWithHeader(final Workspace workspace, final DataSource dataSource,
                                                           final String fileName,
                                                           final Class<T> typeClass) throws IOException {
        MappingIterator<T> iterator = openTsv(workspace, dataSource, fileName, typeClass);
        iterator.next();
        return iterator;
    }

    public static <T> MappingIterator<T> openGzipTsv(final Workspace workspace, final DataSource dataSource,
                                                     final String fileName,
                                                     final Class<T> typeClass) throws IOException {
        InputStream stream = openGzip(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, '\t');
    }
}
