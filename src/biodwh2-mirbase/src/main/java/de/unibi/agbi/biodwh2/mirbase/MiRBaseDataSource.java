package de.unibi.agbi.biodwh2.mirbase;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseGraphExporter;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseMappingDescriber;
import de.unibi.agbi.biodwh2.mirbase.etl.MiRBaseUpdater;

import java.util.Map;

public class MiRBaseDataSource extends DataSource {
    @Override
    public String getId() {
        return "miRBase";
    }

    @Override
    public String getLicense() {
        return "CC0 with attribution";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new MiRBaseUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new MiRBaseGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new MiRBaseMappingDescriber(this);
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final var result = super.getAvailableProperties();
        result.put("speciesFilter", DataSourcePropertyType.INTEGER_LIST);
        return result;
    }
}
