package de.unibi.agbi.biodwh2.disgenet;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetGraphExporter;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetMappingDescriber;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetParser;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetUpdater;

public class DisGeNetDataSource extends DataSource {
    @Override
    public String getId() {
        return "DisGeNET";
    }

    @Override
    public Updater<DisGeNetDataSource> getUpdater() {
        return new DisGeNetUpdater(this);
    }

    @Override
    protected Parser<DisGeNetDataSource> getParser() {
        return new DisGeNetParser(this);
    }

    @Override
    protected GraphExporter<DisGeNetDataSource> getGraphExporter() {
        return new DisGeNetGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new DisGeNetMappingDescriber();
    }

    @Override
    protected void unloadData() {
    }
}
