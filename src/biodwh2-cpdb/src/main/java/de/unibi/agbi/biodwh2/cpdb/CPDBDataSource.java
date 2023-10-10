package de.unibi.agbi.biodwh2.cpdb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.cpdb.etl.CPDBGraphExporter;
import de.unibi.agbi.biodwh2.cpdb.etl.CPDBMappingDescriber;
import de.unibi.agbi.biodwh2.cpdb.etl.CPDBUpdater;

public class CPDBDataSource extends DataSource {
    @Override
    public String getId() {
        return "CPDB";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new CPDBUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new CPDBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new CPDBMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
