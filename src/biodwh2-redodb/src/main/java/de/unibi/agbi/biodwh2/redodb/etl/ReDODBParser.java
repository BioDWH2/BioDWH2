package de.unibi.agbi.biodwh2.redodb.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import de.unibi.agbi.biodwh2.redodb.ReDODBDataSource;
import de.unibi.agbi.biodwh2.redodb.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class ReDODBParser extends SingleFileCsvParser<ReDODBDataSource, Entry> {
    public ReDODBParser(final ReDODBDataSource dataSource) {
        super(dataSource, Entry.class, true, CsvType.TSV, ReDODBUpdater.FILE_NAME);
    }

    @Override
    protected void storeResults(final ReDODBDataSource dataSource, final List<Entry> results) {
        dataSource.entries = results.stream().filter(e -> StringUtils.isNotEmpty(e.drug)).collect(Collectors.toList());
    }
}
