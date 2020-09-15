package de.unibi.agbi.biodwh2.itis;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;

import de.unibi.agbi.biodwh2.itis.etl.*;

public class ITISDataSource extends DataSource {
    @Override
    public String getId() {
        return "ITIS";
    }

    @Override
    public Updater<ITISDataSource> getUpdater() {
        return new ITISUpdater(this);
    }

    @Override
    public Parser<ITISDataSource> getParser() {
        return new ITISParser(this);
    }

    @Override
    public GraphExporter<ITISDataSource> getGraphExporter() {
        return new ITISGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ITISMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
