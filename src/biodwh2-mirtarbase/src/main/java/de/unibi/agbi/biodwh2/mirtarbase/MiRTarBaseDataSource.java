package de.unibi.agbi.biodwh2.mirtarbase;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.mirtarbase.etl.MiRTarBaseGraphExporter;
import de.unibi.agbi.biodwh2.mirtarbase.etl.MiRTarBaseMappingDescriber;
import de.unibi.agbi.biodwh2.mirtarbase.etl.MiRTarBaseUpdater;

public class MiRTarBaseDataSource extends DataSource {
    @Override
    public String getId() {
        return "miRTarBase";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new MiRTarBaseUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new MiRTarBaseGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new MiRTarBaseMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
