package de.unibi.agbi.biodwh2.adrecs;

import de.unibi.agbi.biodwh2.adrecs.etl.ADReCSGraphExporter;
import de.unibi.agbi.biodwh2.adrecs.etl.ADReCSMappingDescriber;
import de.unibi.agbi.biodwh2.adrecs.etl.ADReCSUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;

public class ADReCSDataSource extends DataSource {
    @Override
    public String getId() {
        return "ADReCS";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ADReCSUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ADReCSGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ADReCSMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
