package de.unibi.agbi.biodwh2.unii;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.unii.etl.*;
import de.unibi.agbi.biodwh2.unii.model.UNIIDataEntry;
import de.unibi.agbi.biodwh2.unii.model.UNIIEntry;

import java.util.List;
import java.util.Map;

public class UNIIDataSource extends DataSource {
    public List<UNIIEntry> uniiEntries;
    public Map<String, UNIIDataEntry> uniiDataEntries;

    @Override
    public String getId() {
        return "UNII";
    }

    @Override
    public Updater<UNIIDataSource> getUpdater() {
        return new UNIIUpdater(this);
    }

    @Override
    public Parser<UNIIDataSource> getParser() {
        return new UNIIParser(this);
    }

    @Override
    public GraphExporter<UNIIDataSource> getGraphExporter() {
        return new UNIIGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new UNIIMappingDescriber();
    }

    @Override
    protected void unloadData() {
        uniiEntries = null;
        uniiDataEntries = null;
    }
}
