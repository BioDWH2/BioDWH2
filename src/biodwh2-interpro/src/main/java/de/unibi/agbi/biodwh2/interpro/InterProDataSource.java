package de.unibi.agbi.biodwh2.interpro;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.interpro.etl.InterProGraphExporter;
import de.unibi.agbi.biodwh2.interpro.etl.InterProMappingDescriber;
import de.unibi.agbi.biodwh2.interpro.etl.InterProUpdater;

public class InterProDataSource extends DataSource {
    @Override
    public String getId() {
        return "InterPro";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new InterProUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new InterProGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new InterProMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
