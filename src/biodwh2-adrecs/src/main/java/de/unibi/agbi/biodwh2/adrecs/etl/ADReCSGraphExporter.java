package de.unibi.agbi.biodwh2.adrecs.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.adrecs.ADReCSDataSource;
import de.unibi.agbi.biodwh2.adrecs.model.DrugADREntry;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.*;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.IndexDescription;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipInputStream;

public class ADReCSGraphExporter extends GraphExporter<ADReCSDataSource> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger LOGGER = LoggerFactory.getLogger(ADReCSGraphExporter.class);
    static final String DRUG_LABEL = "Drug";
    static final String ADR_LABEL = "ADR";
    private static final String ADRECS_ID_KEY = "adrecs_id";

    public ADReCSGraphExporter(final ADReCSDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) throws ExporterException {
        graph.addIndex(IndexDescription.forNode(DRUG_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ADR_LABEL, "id", false, IndexDescription.Type.UNIQUE));
        graph.addIndex(IndexDescription.forNode(ADR_LABEL, "adrecs_id", false, IndexDescription.Type.NON_UNIQUE));
        try {
            final XlsxMappingIterator<DrugADREntry> iterator = tryLoadXlsxTable(workspace);
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting ADR associations...");
            final Map<String, Map<String, Map<String, Set<String>>>> adrecsHierarchy = new HashMap<>();
            long counter = 0;
            while (iterator.hasNext()) {
                final DrugADREntry entry = iterator.next();
                counter++;
                if (counter % 100_000 == 0 && LOGGER.isInfoEnabled())
                    LOGGER.info("Progress " + counter + "/" + iterator.getTotalCount());
                if (entry == null)
                    continue;
                final String[] adrecsIdParts = StringUtils.split(entry.adrecsId, '.');
                if (adrecsIdParts.length > 0) {
                    final Map<String, Map<String, Set<String>>> level1 = adrecsHierarchy.computeIfAbsent(
                            adrecsIdParts[0], (k) -> new HashMap<>());
                    if (adrecsIdParts.length > 1) {
                        final Map<String, Set<String>> level2 = level1.computeIfAbsent(adrecsIdParts[1],
                                                                                       (k) -> new HashMap<>());
                        if (adrecsIdParts.length > 2) {
                            final Set<String> level3 = level2.computeIfAbsent(adrecsIdParts[2], (k) -> new HashSet<>());
                            if (adrecsIdParts.length > 3) {
                                level3.add(adrecsIdParts[3]);
                            }
                        }
                    }
                }
                Node adrNode = graph.findNode(ADR_LABEL, "id", entry.adrId);
                if (adrNode == null) {
                    adrNode = graph.addNode(ADR_LABEL, "id", entry.adrId, "term", entry.adrTerm, ADRECS_ID_KEY,
                                            entry.adrecsId, "classification", entry.adrClassification, "meddra_code",
                                            entry.meddraCode);
                }
                Node drugNode = graph.findNode(DRUG_LABEL, "id", entry.drugId);
                if (drugNode == null) {
                    if (StringUtils.isNumeric(entry.pubchemCID))
                        drugNode = graph.addNode(DRUG_LABEL, "id", entry.drugId, "name", entry.drugName, "pubchem_cid",
                                                 entry.pubchemCID);
                    else
                        drugNode = graph.addNode(DRUG_LABEL, "id", entry.drugId, "name", entry.drugName);
                }
                graph.addEdge(drugNode, adrNode, "ASSOCIATED_WITH");
            }
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Exporting ADR hierarchy...");
            for (final String level1Key : adrecsHierarchy.keySet()) {
                final Long[] level1Ids = getNodeIds(graph.findNodes(ADR_LABEL, ADRECS_ID_KEY, level1Key));
                for (final String level2Key : adrecsHierarchy.get(level1Key).keySet()) {
                    final String level2FullKey = level1Key + '.' + level2Key;
                    final Long[] level2Ids = getNodeIds(graph.findNodes(ADR_LABEL, ADRECS_ID_KEY, level2FullKey));
                    for (final long level1Id : level1Ids)
                        for (final long level2Id : level2Ids)
                            graph.addEdge(level2Id, level1Id, "CHILD_OF");
                    for (final String level3Key : adrecsHierarchy.get(level1Key).get(level2Key).keySet()) {
                        final String level3FullKey = level2FullKey + '.' + level3Key;
                        final Long[] level3Ids = getNodeIds(graph.findNodes(ADR_LABEL, ADRECS_ID_KEY, level3FullKey));
                        for (final long level2Id : level2Ids)
                            for (final long level3Id : level3Ids)
                                graph.addEdge(level3Id, level2Id, "CHILD_OF");
                        for (final String level4Key : adrecsHierarchy.get(level1Key).get(level2Key).get(level3Key)) {
                            final String level4FullKey = level3FullKey + '.' + level4Key;
                            final Long[] level4Ids = getNodeIds(
                                    graph.findNodes(ADR_LABEL, ADRECS_ID_KEY, level4FullKey));
                            for (final long level3Id : level3Ids)
                                for (final long level4Id : level4Ids)
                                    graph.addEdge(level4Id, level3Id, "CHILD_OF");
                        }
                    }
                }
            }
        } catch (ParserException e) {
            throw new ExporterFormatException(e);
        }
        return true;
    }

    private XlsxMappingIterator<DrugADREntry> tryLoadXlsxTable(final Workspace workspace) throws ParserException {
        try {
            final ZipInputStream stream = FileUtils.openZip(workspace, dataSource, ADReCSUpdater.FILE_NAME);
            stream.getNextEntry();
            return new XlsxMappingIterator<>(DrugADREntry.class, stream);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException)
                throw new ParserFileNotFoundException(ADReCSUpdater.FILE_NAME);
            else
                throw new ParserFormatException(e);
        }
    }

    private Long[] getNodeIds(Iterable<Node> nodes) {
        final List<Long> ids = new ArrayList<>();
        for (final Node n : nodes)
            ids.add(n.getId());
        return ids.toArray(new Long[0]);
    }

    private static class XlsxMappingIterator<T> implements Iterator<T> {
        private static final int BUFFER_SIZE = 500;
        private final ObjectReader reader;
        private final long totalCount;
        private final Iterator<Row> rows;
        private final String headerRow;
        private final T[] buffer;
        private int usedBufferSize = 0;
        private int bufferIndex = 0;
        private Row nextRow = null;

        public XlsxMappingIterator(final Class<T> type, final InputStream stream) throws IOException {
            final CsvMapper csvMapper = new CsvMapper();
            final CsvSchema schema = csvMapper.schemaFor(type).withColumnSeparator('\t').withQuoteChar('"')
                                              .withNullValue("").withUseHeader(true);
            reader = csvMapper.readerFor(type).with(schema);
            final ReadableWorkbook workbook = new ReadableWorkbook(stream, new ReadingOptions(true, false));
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
            if (cell.getDataFormatString() == null || !cell.getDataFormatString().toLowerCase(Locale.ROOT).contains(
                    "yyyy"))
                tsvBuilder.append(cell.getRawValue());
            else
                tsvBuilder.append('"').append(cell.asDate().format(DATE_FORMATTER)).append('"');
        }
    }
}
