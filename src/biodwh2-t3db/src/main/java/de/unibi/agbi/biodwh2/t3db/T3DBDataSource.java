package de.unibi.agbi.biodwh2.t3db;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.t3db.etl.T3DBGraphExporter;
import de.unibi.agbi.biodwh2.t3db.etl.T3DBMappingDescriber;
import de.unibi.agbi.biodwh2.t3db.etl.T3DBUpdater;

public class T3DBDataSource extends DataSource {
    @Override
    public String getId() {
        return "T3DB";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new T3DBUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new T3DBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new T3DBMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
