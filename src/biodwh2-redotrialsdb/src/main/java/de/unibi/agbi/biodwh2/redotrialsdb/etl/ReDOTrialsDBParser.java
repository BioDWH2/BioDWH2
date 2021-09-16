package de.unibi.agbi.biodwh2.redotrialsdb.etl;

import de.unibi.agbi.biodwh2.core.etl.SingleFileCsvParser;
import de.unibi.agbi.biodwh2.redotrialsdb.ReDOTrialsDBDataSource;
import de.unibi.agbi.biodwh2.redotrialsdb.model.Entry;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class ReDOTrialsDBParser extends SingleFileCsvParser<ReDOTrialsDBDataSource, Entry> {
    public ReDOTrialsDBParser(final ReDOTrialsDBDataSource dataSource) {
        super(dataSource, Entry.class, true, CsvType.TSV, ReDOTrialsDBUpdater.FILE_NAME);
    }

    @Override
    protected void storeResults(final ReDOTrialsDBDataSource dataSource, final List<Entry> results) {
        dataSource.entries = results.stream().filter(e -> StringUtils.isNotEmpty(e.nctNumber)).collect(
                Collectors.toList());
    }
}