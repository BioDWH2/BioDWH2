package de.unibi.agbi.biodwh2.string;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.string.etl.STRINGGraphExporter;
import de.unibi.agbi.biodwh2.string.etl.STRINGMappingDescriber;
import de.unibi.agbi.biodwh2.string.etl.STRINGUpdater;

public class STRINGDataSource extends DataSource {
    @Override
    public String getId() {
        return "STRING";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new STRINGUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new STRINGGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new STRINGMappingDescriber(this);
    }
}
