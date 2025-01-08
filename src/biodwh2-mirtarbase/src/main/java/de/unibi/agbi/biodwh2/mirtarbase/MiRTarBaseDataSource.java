package de.unibi.agbi.biodwh2.mirtarbase;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.mirtarbase.etl.MiRTarBaseGraphExporter;
import de.unibi.agbi.biodwh2.mirtarbase.etl.MiRTarBaseMappingDescriber;
import de.unibi.agbi.biodwh2.mirtarbase.etl.MiRTarBaseUpdater;

import java.util.Map;

public class MiRTarBaseDataSource extends DataSource {
    @Override
    public String getId() {
        return "miRTarBase";
    }

    @Override
    public String getLicenseUrl() {
        return "https://awi.cuhk.edu.cn/~miRTarBase/miRTarBase_2025/cache/download/LICENSE";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new MiRTarBaseUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new MiRTarBaseGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new MiRTarBaseMappingDescriber(this);
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final var result = super.getAvailableProperties();
        result.put("speciesFilter", DataSourcePropertyType.INTEGER_LIST);
        return result;
    }
}
