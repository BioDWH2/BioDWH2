package de.unibi.agbi.biodwh2.opentargets;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.opentargets.etl.OpenTargetsGraphExporter;
import de.unibi.agbi.biodwh2.opentargets.etl.OpenTargetsMappingDescriber;
import de.unibi.agbi.biodwh2.opentargets.etl.OpenTargetsUpdater;

public class OpenTargetsDataSource extends DataSource {
    @Override
    public String getId() {
        return "OpenTargets";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new OpenTargetsUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new OpenTargetsGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new OpenTargetsMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
