package de.unibi.agbi.biodwh2.mirdb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.mirdb.etl.MiRDBGraphExporter;
import de.unibi.agbi.biodwh2.mirdb.etl.MiRDBMappingDescriber;
import de.unibi.agbi.biodwh2.mirdb.etl.MiRDBUpdater;

import java.util.Map;

public class MiRDBDataSource extends DataSource {
    @Override
    public String getId() {
        return "miRDB";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new MiRDBUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new MiRDBGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new MiRDBMappingDescriber(this);
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("scoreThreshold", DataSourcePropertyType.DECIMAL);
        return result;
    }

    @Override
    protected void unloadData() {
    }
}
