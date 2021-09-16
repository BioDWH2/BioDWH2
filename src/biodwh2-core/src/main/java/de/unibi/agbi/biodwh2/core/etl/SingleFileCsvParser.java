package de.unibi.agbi.biodwh2.core.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;

import java.io.IOException;
import java.util.List;

public abstract class SingleFileCsvParser<D extends DataSource, T> extends Parser<D> {
    private final Class<T> typeVariableClass;
    private final boolean hasHeader;
    private final CsvType type;
    private final String fileName;

    protected SingleFileCsvParser(final D dataSource, final Class<T> typeVariableClass, final boolean hasHeader,
                                  final CsvType type, final String fileName) {
        super(dataSource);
        this.typeVariableClass = typeVariableClass;
        this.hasHeader = hasHeader;
        this.type = type;
        this.fileName = fileName;
    }

    @Override
    public final boolean parse(final Workspace workspace) throws ParserException {
        try (MappingIterator<T> iterator = getMappingIterator(workspace, dataSource)) {
            if (hasHeader)
                iterator.next();
            storeResults(dataSource, iterator.readAll());
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file '" + fileName + "'", e);
        }
        return true;
    }

    private MappingIterator<T> getMappingIterator(final Workspace workspace, final D dataSource) throws IOException {
        return type == CsvType.CSV ? FileUtils.openCsv(workspace, dataSource, fileName, typeVariableClass) :
               FileUtils.openTsv(workspace, dataSource, fileName, typeVariableClass);
    }

    protected abstract void storeResults(final D dataSource, final List<T> results);

    protected enum CsvType {
        CSV,
        TSV
    }
}
