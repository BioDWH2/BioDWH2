package de.unibi.agbi.biodwh2.cancerdrugsdb.etl;

import de.unibi.agbi.biodwh2.cancerdrugsdb.CancerDrugsDBDataSource;
import de.unibi.agbi.biodwh2.cancerdrugsdb.model.Entry;
import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class CancerDrugsDBParser extends SingleFileCsvParser<CancerDrugsDBDataSource, Entry> {
    public CancerDrugsDBParser(final CancerDrugsDBDataSource dataSource) {
        super(dataSource, Entry.class, true, CsvType.TSV, "cancerdrugsdb.txt");
    }

    @Override
    protected void storeResults(final CancerDrugsDBDataSource dataSource, final List<Entry> results) {
        dataSource.entries = results.stream().filter(e -> StringUtils.isNotEmpty(e.product)).collect(
                Collectors.toList());
    }
}
