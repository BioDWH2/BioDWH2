package de.unibi.agbi.biodwh2.opentargets.etl;

import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.column.ColumnReader;
import org.apache.parquet.column.impl.ColumnReadStoreImpl;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.example.DummyRecordConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.FileMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.PrimitiveType;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class ParquetReader implements Spliterator<Map<String, Object>>, Closeable {
    private final ParquetFileReader reader;
    private final List<ColumnDescriptor> columns;
    private final FileMetaData metaData;
    private final GroupConverter recordConverter;
    private boolean finished;
    private long currentRowGroupSize = -1;
    private List<ColumnReader> currentRowGroupColumnReaders;
    private long currentRowIndex = -1;

    public static Stream<Map<String, Object>> streamContent(InputFile file) throws IOException {
        final var reader = new ParquetReader(file);
        return StreamSupport.stream(reader, false).onClose(() -> closeSilently(reader));
    }

    public static ParquetMetadata readMetadata(InputFile file) throws IOException {
        try (ParquetFileReader reader = ParquetFileReader.open(file)) {
            return reader.getFooter();
        }
    }

    private ParquetReader(InputFile file) throws IOException {
        reader = ParquetFileReader.open(file);
        metaData = reader.getFooter().getFileMetaData();
        recordConverter = new DummyRecordConverter(metaData.getSchema()).getRootConverter();
        columns = new ArrayList<>(metaData.getSchema().getColumns());
    }

    private static void closeSilently(final Closeable resource) {
        try {
            resource.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public boolean tryAdvance(final Consumer<? super Map<String, Object>> action) {
        if (!finished && currentRowIndex == currentRowGroupSize)
            advanceToNextRowGroup();
        if (finished)
            return false;
        action.accept(readRecord());
        currentRowIndex++;
        return true;
    }

    private void advanceToNextRowGroup() {
        final PageReadStore rowGroup;
        try {
            rowGroup = reader.readNextRowGroup();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read parquet", e);
        }
        if (rowGroup == null) {
            finished = true;
            return;
        }
        final var columnReadStore = new ColumnReadStoreImpl(rowGroup, recordConverter, metaData.getSchema(),
                                                            metaData.getCreatedBy());
        currentRowGroupSize = rowGroup.getRowCount();
        currentRowGroupColumnReaders = columns.stream().map(columnReadStore::getColumnReader).collect(
                Collectors.toList());
        currentRowIndex = 0;
    }

    private Map<String, Object> readRecord() {
        final Map<String, Object> record = new HashMap<>();
        for (final ColumnReader columnReader : currentRowGroupColumnReaders) {
            do {
                final var value = readValue(columnReader);
                if (value != null)
                    record.put(columnReader.getDescriptor().getPath()[0], value);
                columnReader.consume();
            } while (columnReader.getCurrentRepetitionLevel() != 0);
            if (columnReader.getCurrentRepetitionLevel() != 0)
                throw new IllegalStateException("Unexpected repetition");
        }
        return record;
    }

    private static Object readValue(final ColumnReader columnReader) {
        final ColumnDescriptor column = columnReader.getDescriptor();
        if (columnReader.getCurrentDefinitionLevel() != column.getMaxDefinitionLevel())
            return null;
        final PrimitiveType primitiveType = column.getPrimitiveType();
        switch (primitiveType.getPrimitiveTypeName()) {
            case BINARY:
            case FIXED_LEN_BYTE_ARRAY:
            case INT96:
                return primitiveType.stringifier().stringify(columnReader.getBinary());
            case BOOLEAN:
                return columnReader.getBoolean();
            case DOUBLE:
                return columnReader.getDouble();
            case FLOAT:
                return columnReader.getFloat();
            case INT32:
                return columnReader.getInteger();
            case INT64:
                return columnReader.getLong();
            default:
                throw new IllegalArgumentException("Unsupported type: " + primitiveType);
        }
    }

    @Override
    public Spliterator<Map<String, Object>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return reader.getRecordCount();
    }

    @Override
    public int characteristics() {
        return ORDERED | NONNULL | DISTINCT;
    }
}
