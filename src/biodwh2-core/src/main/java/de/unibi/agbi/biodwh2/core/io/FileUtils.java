package de.unibi.agbi.biodwh2.core.io;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

@SuppressWarnings("unused")
public final class FileUtils {
    private FileUtils() {
    }

    public static BufferedOutputStream openOutput(final String filePath) throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)));
    }

    public static BufferedInputStream openInput(final Workspace workspace, final DataSource dataSource,
                                                final String fileName) throws IOException {
        return openInput(dataSource.resolveSourceFilePath(workspace, fileName));
    }

    public static BufferedInputStream openInput(final String filePath) throws IOException {
        return new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
    }

    public static GZIPInputStream openGzip(final Workspace workspace, final DataSource dataSource,
                                           final String fileName) throws IOException {
        return new GZIPInputStream(openInput(workspace, dataSource, fileName));
    }

    public static TarArchiveInputStream openTar(final Workspace workspace, final DataSource dataSource,
                                                final String fileName) throws IOException {
        return new TarArchiveInputStream(openInput(workspace, dataSource, fileName));
    }

    public static TarArchiveInputStream openTarGzip(final Workspace workspace, final DataSource dataSource,
                                                    final String fileName) throws IOException {
        return new TarArchiveInputStream(openGzip(workspace, dataSource, fileName));
    }

    public static ZipInputStream openZip(final Workspace workspace, final DataSource dataSource,
                                         final String fileName) throws IOException {
        return new ZipInputStream(openInput(workspace, dataSource, fileName));
    }

    public static <T> MappingIterator<T> openCsv(final Workspace workspace, final DataSource dataSource,
                                                 final String fileName, final Class<T> typeClass) throws IOException {
        final InputStream stream = openInput(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, ',', false);
    }

    public static <T> MappingIterator<T> openCsvWithHeader(final Workspace workspace, final DataSource dataSource,
                                                           final String fileName,
                                                           final Class<T> typeClass) throws IOException {
        final InputStream stream = openInput(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, ',', true);
    }

    public static <T> MappingIterator<T> openSeparatedValuesFile(final InputStream stream, final Class<T> typeClass,
                                                                 final char separator,
                                                                 final boolean withHeader) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return getFormatReader(typeClass, separator, withHeader, true).readValues(reader);
    }

    private static <T> ObjectReader getFormatReader(final Class<T> typeClass, final char separator,
                                                    final boolean withHeader, final boolean withQuoting) {
        final CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(typeClass).withColumnSeparator(separator).withNullValue("")
                                    .withUseHeader(withHeader);
        if (!withQuoting)
            schema = schema.withoutQuoteChar();
        if (typeClass == String[].class)
            csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        csvMapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        csvMapper.disable(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS);
        csvMapper.enable(CsvParser.Feature.ALLOW_COMMENTS);
        return csvMapper.readerFor(typeClass).with(schema);
    }

    public static <T> MappingIterator<T> openSeparatedValuesFile(final InputStream stream, final Class<T> typeClass,
                                                                 final char separator, final boolean withHeader,
                                                                 final boolean withQuoting) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return getFormatReader(typeClass, separator, withHeader, withQuoting).readValues(reader);
    }

    public static <T> MappingIterator<T> openGzipCsv(final Workspace workspace, final DataSource dataSource,
                                                     final String fileName,
                                                     final Class<T> typeClass) throws IOException {
        final InputStream stream = openGzip(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, ',', false);
    }

    public static <T> MappingIterator<T> openGzipCsvWithHeader(final Workspace workspace, final DataSource dataSource,
                                                               final String fileName,
                                                               final Class<T> typeClass) throws IOException {
        final InputStream stream = openGzip(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, ',', true);
    }

    public static <T> MappingIterator<T> openTsv(final Workspace workspace, final DataSource dataSource,
                                                 final String fileName, final Class<T> typeClass) throws IOException {
        final InputStream stream = openInput(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, '\t', false);
    }

    public static <T> MappingIterator<T> openTsvWithHeader(final Workspace workspace, final DataSource dataSource,
                                                           final String fileName,
                                                           final Class<T> typeClass) throws IOException {
        final InputStream stream = openInput(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, '\t', true);
    }

    public static <T> MappingIterator<T> openTsvWithHeaderWithoutQuoting(final Workspace workspace,
                                                                         final DataSource dataSource,
                                                                         final String fileName,
                                                                         final Class<T> typeClass) throws IOException {
        final InputStream stream = openInput(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, '\t', true, false);
    }

    public static <T> MappingIterator<T> openGzipTsv(final Workspace workspace, final DataSource dataSource,
                                                     final String fileName,
                                                     final Class<T> typeClass) throws IOException {
        final InputStream stream = openGzip(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, '\t', false);
    }

    public static <T> MappingIterator<T> openGzipTsvWithHeader(final Workspace workspace, final DataSource dataSource,
                                                               final String fileName,
                                                               final Class<T> typeClass) throws IOException {
        final InputStream stream = openGzip(workspace, dataSource, fileName);
        return openSeparatedValuesFile(stream, typeClass, '\t', true);
    }

    public static boolean writeTextToUTF8File(final Path path, final String text) {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(path.toFile(), text, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean safeDelete(final String filePath) {
        return safeDelete(Paths.get(filePath));
    }

    public static boolean safeDelete(final Path filePath) {
        if (Files.exists(filePath))
            try {
                Files.delete(filePath);
            } catch (IOException ignored) {
                return false;
            }
        return true;
    }

    public static BufferedReader createBufferedReaderFromStream(final InputStream stream) {
        return createBufferedReaderFromStream(stream, StandardCharsets.UTF_8);
    }

    public static BufferedReader createBufferedReaderFromStream(final InputStream stream, final Charset charset) {
        final InputStreamReader reader = new InputStreamReader(stream, charset);
        return new BufferedReader(reader);
    }

    public static BufferedWriter createBufferedWriterFromStream(final OutputStream stream) {
        return createBufferedWriterFromStream(stream, StandardCharsets.UTF_8);
    }

    public static BufferedWriter createBufferedWriterFromStream(final OutputStream stream, final Charset charset) {
        final OutputStreamWriter writer = new OutputStreamWriter(stream, charset);
        return new BufferedWriter(writer);
    }
}
