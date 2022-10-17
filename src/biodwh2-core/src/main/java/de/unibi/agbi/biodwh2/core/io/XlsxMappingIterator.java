package de.unibi.agbi.biodwh2.core.io;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;

public class XlsxMappingIterator<T> implements Iterator<T>, AutoCloseable {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int BUFFER_SIZE = 500;
    private final ObjectReader reader;
    private final ReadableWorkbook workbook;
    private final long totalCount;
    private final Iterator<Row> rows;
    private final String headerRow;
    private final T[] buffer;
    private int usedBufferSize = 0;
    private int bufferIndex = 0;
    private Row nextRow = null;

    public XlsxMappingIterator(final Class<T> type, final InputStream stream) throws IOException {
        final CsvMapper csvMapper = new CsvMapper();
        final CsvSchema schema = csvMapper.schemaFor(type).withColumnSeparator('\t').withQuoteChar('"').withNullValue(
                "").withUseHeader(true);
        reader = csvMapper.readerFor(type).with(schema);
        workbook = new ReadableWorkbook(stream, new ReadingOptions(true, false));
        final Sheet sheet = workbook.getFirstSheet();
        totalCount = sheet.openStream().count();
        rows = sheet.openStream().iterator();
        advanceToNextRow();
        final StringBuilder headerBuilder = new StringBuilder();
        appendRow(headerBuilder, nextRow);
        headerRow = headerBuilder.toString();
        //noinspection unchecked
        buffer = (T[]) Array.newInstance(type, BUFFER_SIZE);
    }

    public long getTotalCount() {
        return totalCount;
    }

    @Override
    public boolean hasNext() {
        fillBuffer();
        return bufferIndex < usedBufferSize;
    }

    private void advanceToNextRow() {
        nextRow = null;
        while (rows.hasNext()) {
            nextRow = rows.next();
            if (nextRow.getPhysicalCellCount() == 0)
                nextRow = null;
            else
                break;
        }
    }

    private void fillBuffer() {
        if (bufferIndex >= usedBufferSize) {
            bufferIndex = 0;
            usedBufferSize = 0;
            final StringBuilder tsvBuilder = new StringBuilder(headerRow);
            advanceToNextRow();
            while (nextRow != null && usedBufferSize < BUFFER_SIZE) {
                appendRow(tsvBuilder, nextRow);
                usedBufferSize++;
                if (usedBufferSize < BUFFER_SIZE)
                    advanceToNextRow();
            }
            try {
                usedBufferSize = 0;
                final MappingIterator<T> entries = reader.readValues(tsvBuilder.toString());
                while (entries.hasNext()) {
                    buffer[usedBufferSize] = entries.next();
                    usedBufferSize++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public T next() {
        if (bufferIndex >= usedBufferSize)
            return null;
        final T nextValue = buffer[bufferIndex];
        bufferIndex++;
        return nextValue;
    }

    private void appendRow(final StringBuilder tsvBuilder, final Row row) throws RuntimeException {
        boolean firstCell = true;
        for (final Cell cell : row) {
            if (!firstCell)
                tsvBuilder.append('\t');
            firstCell = false;
            if (cell.getType() == CellType.STRING)
                appendStringCell(tsvBuilder, StringUtils.strip(cell.asString(), " \t\u00A0"));
            if (cell.getType() == CellType.BOOLEAN)
                tsvBuilder.append(cell.asBoolean());
            if (cell.getType() == CellType.NUMBER)
                appendNumericCell(tsvBuilder, cell);
            if (cell.getType() == CellType.FORMULA)
                throw new RuntimeException("Unable to parse XLSX formula cell value");
        }
        tsvBuilder.append('\n');
    }

    private void appendStringCell(final StringBuilder tsvBuilder, final String value) {
        tsvBuilder.append('"').append(StringUtils.replace(value, "\"", "\"\"")).append('"');
    }

    private void appendNumericCell(final StringBuilder tsvBuilder, final Cell cell) {
        // TODO: better date format check
        if (cell.getDataFormatString() == null || !cell.getDataFormatString().toLowerCase(Locale.ROOT).contains("yyyy"))
            tsvBuilder.append(cell.getRawValue());
        else
            tsvBuilder.append('"').append(cell.asDate().format(DATE_FORMATTER)).append('"');
    }

    @Override
    public void close() throws Exception {
        workbook.close();
    }
}
