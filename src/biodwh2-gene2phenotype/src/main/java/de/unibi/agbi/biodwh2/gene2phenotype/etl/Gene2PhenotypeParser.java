package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.exceptions.ParserException;
import de.unibi.agbi.biodwh2.core.exceptions.ParserFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.gene2phenotype.Gene2PhenotypeDataSource;
import de.unibi.agbi.biodwh2.gene2phenotype.model.GeneDiseasePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Gene2PhenotypeParser extends Parser<Gene2PhenotypeDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Gene2PhenotypeParser.class);

    public Gene2PhenotypeParser(final Gene2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean parse(final Workspace workspace) throws ParserException {
        for (final String fileName : Gene2PhenotypeUpdater.FILE_NAMES) {
            LOGGER.info("Parsing " + fileName + "...");
            parseFile(workspace, fileName);
        }
        return true;
    }

    private void parseFile(final Workspace workspace, final String fileName) throws ParserException {
        try {
            final MappingIterator<GeneDiseasePair> iterator = FileUtils.openGzipCsvWithHeader(workspace, dataSource,
                                                                                              fileName,
                                                                                              GeneDiseasePair.class);
            dataSource.geneDiseasePairs.addAll(iterator.readAll());
        } catch (IOException e) {
            throw new ParserFormatException(e);
        }
    }
}
