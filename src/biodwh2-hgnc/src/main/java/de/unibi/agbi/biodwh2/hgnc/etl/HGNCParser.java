package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

import java.util.List;

public class HGNCParser extends SingleFileCsvParser<HGNCDataSource, Gene> {
    public HGNCParser(final HGNCDataSource dataSource) {
        super(dataSource, Gene.class, true, CsvType.TSV, HGNCUpdater.FILE_NAME);
    }

    @Override
    protected void storeResults(final HGNCDataSource dataSource, final List<Gene> results) {
        dataSource.genes = results;
    }
}
