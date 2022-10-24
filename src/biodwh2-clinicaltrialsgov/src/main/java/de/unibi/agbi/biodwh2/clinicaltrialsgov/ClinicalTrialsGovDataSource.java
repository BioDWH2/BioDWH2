package de.unibi.agbi.biodwh2.clinicaltrialsgov;

import de.unibi.agbi.biodwh2.clinicaltrialsgov.etl.ClinicalTrialsGovGraphExporter;
import de.unibi.agbi.biodwh2.clinicaltrialsgov.etl.ClinicalTrialsGovMappingDescriber;
import de.unibi.agbi.biodwh2.clinicaltrialsgov.etl.ClinicalTrialsGovUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;

import java.util.Map;

public class ClinicalTrialsGovDataSource extends DataSource {
    @Override
    public String getId() {
        return "ClinicalTrials.gov";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ClinicalTrialsGovUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ClinicalTrialsGovGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ClinicalTrialsGovMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("updateIntervalDays", DataSourcePropertyType.INTEGER);
        return result;
    }
}
