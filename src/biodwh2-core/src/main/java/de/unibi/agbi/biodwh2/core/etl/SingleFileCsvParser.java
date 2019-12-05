package de.unibi.agbi.biodwh2.core.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class SingleFileCsvParser<T> extends Parser {
    private final Class<T> typeVariableClass;
    private final boolean hasHeader;
    private final CsvType type;
    private final String fileName;

    protected SingleFileCsvParser(Class<T> typeVariableClass, boolean hasHeader, CsvType type, String fileName) {
        this.typeVariableClass = typeVariableClass;
        this.hasHeader = hasHeader;
        this.type = type;
        this.fileName = fileName;
    }

    @Override
    public boolean parse(Workspace workspace, DataSource dataSource) throws ParserException {
        String filePath = dataSource.resolveSourceFilePath(workspace, fileName);
        File sourceFile = new File(filePath);
        if (!sourceFile.exists())
            throw new ParserFileNotFoundException(fileName);
        ObjectReader reader = getFormatReader();
        try {
            MappingIterator<T> iterator = reader.readValues(sourceFile);
            if (hasHeader)
                iterator.next();
            storeResults(dataSource, iterator.readAll());
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + fileName + "'", e);
        }
        return true;
    }

    private ObjectReader getFormatReader() {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(typeVariableClass).withColumnSeparator(getSeparator()).withNullValue("");
        return csvMapper.readerFor(typeVariableClass).with(schema);
    }

    private char getSeparator() {
        switch (type) {
            default:
            case CSV:
                return ',';
            case TSV:
                return '\t';
        }
    }

    protected abstract void storeResults(DataSource dataSource, List<T> results);

    protected enum CsvType {
        CSV,
        TSV
    }
}
