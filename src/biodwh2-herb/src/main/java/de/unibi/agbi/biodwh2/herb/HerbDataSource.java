package de.unibi.agbi.biodwh2.herb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.herb.etl.HerbGraphExporter;
import de.unibi.agbi.biodwh2.herb.etl.HerbMappingDescriber;
import de.unibi.agbi.biodwh2.herb.etl.HerbUpdater;

public class HerbDataSource extends DataSource {
    @Override
    public String getId() {
        return "HERB";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new HerbUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new HerbGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new HerbMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
