package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

import java.util.List;

public class HGNCParser extends SingleFileCsvParser<HGNCDataSource, Gene> {
    public HGNCParser() {
        super(Gene.class, true, CsvType.TSV, "hgnc_complete_set.txt");
    }

    @Override
    protected void storeResults(HGNCDataSource dataSource, List<Gene> results) {
        dataSource.genes = results;
    }
}
