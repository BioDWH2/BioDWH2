package de.unibi.agbi.biodwh2.hgnc.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import de.unibi.agbi.biodwh2.hgnc.HGNCDataSource;
import de.unibi.agbi.biodwh2.hgnc.model.Gene;

import java.util.List;

public class HGNCParser extends SingleFileCsvParser<Gene> {
    public HGNCParser() {
        super(Gene.class, true, CsvType.TSV, "hgnc_complete_set.txt");
    }

    @Override
    protected void storeResults(DataSource dataSource, List<Gene> results) {
        ((HGNCDataSource) dataSource).genes = results;
    }
}
