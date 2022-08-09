package de.unibi.agbi.biodwh2.unii;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.unii.etl.UNIIGraphExporter;
import de.unibi.agbi.biodwh2.unii.etl.UNIIMappingDescriber;
import de.unibi.agbi.biodwh2.unii.etl.UNIIParser;
import de.unibi.agbi.biodwh2.unii.etl.UNIIUpdater;
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
    public String getFullName() {
        return "FDA Unique Ingredient Identifier (UNII)";
    }

    @Override
    public String getDescription() {
        return "FDA - Substance Registration System - Unique Ingredient Identifier (UNII)";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
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
        return new UNIIMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        uniiEntries = null;
        uniiDataEntries = null;
    }
}
