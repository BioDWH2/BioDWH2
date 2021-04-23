package de.unibi.agbi.biodwh2.cancerdrugsdb.etl;

import de.unibi.agbi.biodwh2.cancerdrugsdb.CancerDrugsDBDataSource;
import de.unibi.agbi.biodwh2.cancerdrugsdb.model.Entry;
import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;

import java.util.List;

public class CancerDrugsDBParser extends SingleFileCsvParser<CancerDrugsDBDataSource, Entry> {
    public CancerDrugsDBParser(final CancerDrugsDBDataSource dataSource) {
        super(dataSource, Entry.class, true, CsvType.TSV, "cancerdrugsdb.txt");
    }

    @Override
    protected void storeResults(final CancerDrugsDBDataSource dataSource, final List<Entry> results) {
        dataSource.entries = results;
    }
}
