package de.unibi.agbi.biodwh2.mirbase;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseGraphExporter;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseMappingDescriber;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseUpdater;

public class MiRBaseDataSource extends DataSource {
    @Override
    public String getId() {
        return "miRBase";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new MiRBaseUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new MiRBaseGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new MiRBaseMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
