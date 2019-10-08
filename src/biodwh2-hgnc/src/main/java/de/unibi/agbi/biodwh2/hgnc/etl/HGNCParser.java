package de.unibi.agbi.biodwh2.hgnc.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFileNotFoundException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

import java.io.File;
import java.io.IOException;

public class HGNCParser extends Parser {
    @Override
    public boolean parse(Workspace workspace, DataSource dataSource) throws ParserException {
        String filePath = dataSource.resolveSourceFilePath(workspace, "hgnc_complete_set.txt");
        File hgncSetFile = new File(filePath);
        if (!hgncSetFile.exists())
            throw new ParserFileNotFoundException("hgnc_complete_set.txt");
        ObjectReader reader = getTsvReader();
        try {
            MappingIterator<Gene> iterator = reader.readValues(hgncSetFile);
            iterator.next();
            ((HGNCDataSource) dataSource).genes = iterator.readAll();
        } catch (IOException e) {
            throw new ParserFormatException("Failed to parse the file 'hgnc_complete_set.txt'", e);
        }
        return true;
    }

    private ObjectReader getTsvReader() {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(Gene.class).withColumnSeparator('\t').withNullValue("");
        return csvMapper.readerFor(Gene.class).with(schema);
    }
}
