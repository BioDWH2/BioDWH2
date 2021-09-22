package de.unibi.agbi.biodwh2.aact;

import de.unibi.agbi.biodwh2.aact.etl.AACTGraphExporter;
import de.unibi.agbi.biodwh2.aact.etl.AACTMappingDescriber;
import de.unibi.agbi.biodwh2.aact.etl.AACTUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;

public class AACTDataSource extends DataSource {
    @Override
    public String getId() {
        return "AACT";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new AACTUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new AACTGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new AACTMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
