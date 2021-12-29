package de.unibi.agbi.biodwh2.stitch;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.stitch.etl.STITCHGraphExporter;
import de.unibi.agbi.biodwh2.stitch.etl.STITCHMappingDescriber;
import de.unibi.agbi.biodwh2.stitch.etl.STITCHUpdater;

public class STITCHDataSource extends DataSource {
    @Override
    public String getId() {
        return "STITCH";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new STITCHUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new STITCHGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new STITCHMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
