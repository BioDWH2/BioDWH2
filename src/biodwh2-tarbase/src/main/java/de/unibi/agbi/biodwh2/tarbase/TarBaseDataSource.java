package de.unibi.agbi.biodwh2.tarbase;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.tarbase.etl.TarBaseGraphExporter;
import de.unibi.agbi.biodwh2.tarbase.etl.TarBaseMappingDescriber;
import de.unibi.agbi.biodwh2.tarbase.etl.TarBaseUpdater;

import java.util.Map;

public class TarBaseDataSource extends DataSource {
    @Override
    public String getId() {
        return "TarBase";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new TarBaseUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new TarBaseGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new TarBaseMappingDescriber(this);
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("downloadUrl", DataSourcePropertyType.STRING);
        return result;
    }
}
