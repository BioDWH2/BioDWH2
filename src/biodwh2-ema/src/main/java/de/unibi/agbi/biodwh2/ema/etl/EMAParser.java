package de.unibi.agbi.biodwh2.ema.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.ema.EMADataSource;
import de.unibi.agbi.biodwh2.ema.model.EPAREntry;
import de.unibi.agbi.biodwh2.ema.model.HMPCEntry;
import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class EMAParser extends Parser<EMADataSource> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public EMAParser(final EMADataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        dataSource.EPAREntries = tryLoadXlsxTable(workspace, EMAUpdater.EPAR_TABLE_FILE_NAME, EPAREntry.class);
        dataSource.HMPCEntries = tryLoadXlsxTable(workspace, EMAUpdater.HMPC_TABLE_FILE_NAME, HMPCEntry.class);
        return true;
    }

    private <T> List<T> tryLoadXlsxTable(final Workspace workspace, final String fileName,
                                         final Class<T> type) throws ParserException {
        try {
            return loadXlsxTable(workspace, fileName, type);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException)
                throw new ParserFileNotFoundException(fileName);
            else
                throw new ParserFormatException(e);
        }
    }

    private <T> List<T> loadXlsxTable(final Workspace workspace, final String fileName,
                                      final Class<T> type) throws IOException, ParserFormatException {
        final String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        final FileInputStream file = new FileInputStream(filePath);
        final ReadableWorkbook workbook = new ReadableWorkbook(file, new ReadingOptions(true, false));
        final Sheet sheet = workbook.getFirstSheet();
        final String tsvContent = getTsvContentFromSheet(sheet);
        return readAllEntriesFromTsvString(type, tsvContent);
    }

    private String getTsvContentFromSheet(final Sheet sheet) throws ParserFormatException, IOException {
        boolean foundHeaderCell = false;
        final StringBuilder tsvBuilder = new StringBuilder();
        for (final Row row : sheet.read()) {
            if (row.getPhysicalCellCount() == 0)
                continue;
            if (!foundHeaderCell) {
                final Optional<Cell> firstCell = row.getOptionalCell(0);
                final String firstCellValue = firstCell.map(Cell::getRawValue).orElse(null);
                if ("Category".equals(firstCellValue) || "Status".equals(firstCellValue))
                    foundHeaderCell = true;
            }
            if (foundHeaderCell)
                appendRow(tsvBuilder, row);
        }
        return StringUtils.stripEnd(tsvBuilder.toString(), "\n");
    }

    private void appendRow(final StringBuilder tsvBuilder, final Row row) throws ParserFormatException {
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
                throw new ParserFormatException("Unable to parse XLSX formula cell value");
        }
        tsvBuilder.append('\n');
    }

    private void appendStringCell(final StringBuilder tsvBuilder, String value) {
        tsvBuilder.append('"').append(StringUtils.replace(value, "\"", "\"\"")).append('"');
    }

    private void appendNumericCell(final StringBuilder tsvBuilder, final Cell cell) {
        // TODO: better date format check
        if (cell.getDataFormatString() == null || !cell.getDataFormatString().toLowerCase(Locale.ROOT).contains("yyyy"))
            tsvBuilder.append(cell.getRawValue());
        else
            tsvBuilder.append('"').append(cell.asDate().format(DATE_FORMATTER)).append('"');
    }

    private <T> List<T> readAllEntriesFromTsvString(final Class<T> type, final String tsvContent) throws IOException {
        final CsvMapper csvMapper = new CsvMapper();
        final CsvSchema schema = csvMapper.schemaFor(type).withColumnSeparator('\t').withQuoteChar('"').withNullValue(
                "").withUseHeader(true);
        final ObjectReader reader = csvMapper.readerFor(type).with(schema);
        final MappingIterator<T> iterator = reader.readValues(tsvContent);
        return iterator.readAll();
    }
}
