package de.unibi.agbi.biodwh2.hmdb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBGraphExporter;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBMappingDescriber;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBUpdater;

public class HMDBDataSource extends DataSource {
    @Override
    public String getId() {
        return "HMDB";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new HMDBUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new HMDBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new HMDBMappingDescriber(this);
    }
}
