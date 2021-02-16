package de.unibi.agbi.biodwh2.core.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TableFormatter {
    private static final int PADDING = 1;
    private static final String NEW_LINE_CHARACTER = "\n";
    private static final String JOINT_CHARACTER = "+";
    private static final String VERTICAL_SPLIT_CHARACTER = "|";
    private static final String HORIZONTAL_SPLIT_CHARACTER = "-";

    private final boolean centerText;

    public TableFormatter() {
        this(true);
    }

    public TableFormatter(final boolean centerText) {
        this.centerText = centerText;
    }

    public String format(final List<String> headers, final List<List<String>> rows) {
        final StringBuilder builder = new StringBuilder();
        final Map<Integer, Integer> columnMaxWidthMapping = getMaximumTableWidth(headers, rows);
        builder.append(NEW_LINE_CHARACTER);
        createRowLine(builder, headers.size(), columnMaxWidthMapping);
        builder.append(NEW_LINE_CHARACTER);
        for (int i = 0; i < headers.size(); i++)
            fillCell(builder, headers.get(i), i, columnMaxWidthMapping);
        builder.append(NEW_LINE_CHARACTER);
        createRowLine(builder, headers.size(), columnMaxWidthMapping);
        for (final List<String> row : rows) {
            builder.append(NEW_LINE_CHARACTER);
            for (int i = 0; i < row.size(); i++)
                fillCell(builder, row.get(i), i, columnMaxWidthMapping);
        }
        builder.append(NEW_LINE_CHARACTER);
        createRowLine(builder, headers.size(), columnMaxWidthMapping);
        builder.append(NEW_LINE_CHARACTER);
        return builder.toString();
    }

    private Map<Integer, Integer> getMaximumTableWidth(final List<String> headers, final List<List<String>> rows) {
        final Map<Integer, Integer> columnMaxWidthMapping = new HashMap<>();
        for (int i = 0; i < headers.size(); i++)
            columnMaxWidthMapping.put(i, 0);
        for (int i = 0; i < headers.size(); i++)
            if (headers.get(i).length() > columnMaxWidthMapping.get(i))
                columnMaxWidthMapping.put(i, headers.get(i).length());
        for (final List<String> row : rows)
            for (int i = 0; i < row.size(); i++)
                if (row.get(i).length() > columnMaxWidthMapping.get(i))
                    columnMaxWidthMapping.put(i, row.get(i).length());
        for (int i = 0; i < headers.size(); i++)
            if (columnMaxWidthMapping.get(i) % 2 != 0)
                columnMaxWidthMapping.put(i, columnMaxWidthMapping.get(i) + 1);
        return columnMaxWidthMapping;
    }

    private void createRowLine(final StringBuilder builder, final int headersSize,
                               final Map<Integer, Integer> columnMaxWidthMapping) {
        for (int i = 0; i < headersSize; i++) {
            if (i == 0)
                builder.append(JOINT_CHARACTER);
            for (int j = 0; j < columnMaxWidthMapping.get(i) + PADDING * 2; j++)
                builder.append(HORIZONTAL_SPLIT_CHARACTER);
            builder.append(JOINT_CHARACTER);
        }
    }

    private void fillCell(final StringBuilder builder, final String cell, final int cellIndex,
                          final Map<Integer, Integer> columnMaxWidthMapping) {
        final int cellPaddingSize = getOptimumCellPadding(cellIndex, cell.length(), columnMaxWidthMapping);
        if (cellIndex == 0)
            builder.append(VERTICAL_SPLIT_CHARACTER);
        fillSpace(builder, centerText ? cellPaddingSize : 1);
        builder.append(cell);
        if (cell.length() % 2 != 0)
            builder.append(' ');
        fillSpace(builder, centerText ? cellPaddingSize : cellPaddingSize * 2 - 1);
        builder.append(VERTICAL_SPLIT_CHARACTER);
    }

    private int getOptimumCellPadding(final int cellIndex, final int dataLength,
                                      final Map<Integer, Integer> columnMaxWidthMapping) {
        int paddedDataLength = dataLength;
        if (paddedDataLength % 2 != 0)
            paddedDataLength++;
        int cellPaddingSize = PADDING;
        if (paddedDataLength < columnMaxWidthMapping.get(cellIndex))
            cellPaddingSize = cellPaddingSize + (columnMaxWidthMapping.get(cellIndex) - paddedDataLength) / 2;
        return cellPaddingSize;
    }

    private void fillSpace(final StringBuilder builder, final int length) {
        for (int i = 0; i < length; i++)
            builder.append(' ');
    }
}
