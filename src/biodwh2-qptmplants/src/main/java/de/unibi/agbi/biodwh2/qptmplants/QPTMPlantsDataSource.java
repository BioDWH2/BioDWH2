package de.unibi.agbi.biodwh2.qptmplants;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.qptmplants.etl.QPTMPlantsGraphExporter;
import de.unibi.agbi.biodwh2.qptmplants.etl.QPTMPlantsMappingDescriber;
import de.unibi.agbi.biodwh2.qptmplants.etl.QPTMPlantsUpdater;

import java.util.Map;

@SuppressWarnings("unused")
public class QPTMPlantsDataSource extends DataSource {
    @Override
    public String getId() {
        return "qPTMplants";
    }

    @Override
    public String getLicense() {
        return "Free for academic research";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new QPTMPlantsUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new QPTMPlantsGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new QPTMPlantsMappingDescriber(this);
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final var result = super.getAvailableProperties();
        result.put("downloadUrl", DataSourcePropertyType.STRING);
        return result;
    }
}
