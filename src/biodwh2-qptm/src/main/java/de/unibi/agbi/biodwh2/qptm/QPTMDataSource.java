package de.unibi.agbi.biodwh2.qptm;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.qptm.etl.QPTMGraphExporter;
import de.unibi.agbi.biodwh2.qptm.etl.QPTMMappingDescriber;
import de.unibi.agbi.biodwh2.qptm.etl.QPTMUpdater;

import java.util.Map;

@SuppressWarnings("unused")
public class QPTMDataSource extends DataSource {
    @Override
    public String getId() {
        return "qPTM";
    }

    @Override
    public String getLicense() {
        return "Free for academic research";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new QPTMUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new QPTMGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new QPTMMappingDescriber(this);
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final var result = super.getAvailableProperties();
        result.put("downloadUrl", DataSourcePropertyType.STRING);
        return result;
    }
}
