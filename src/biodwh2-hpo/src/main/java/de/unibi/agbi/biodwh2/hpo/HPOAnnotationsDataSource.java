package de.unibi.agbi.biodwh2.hpo;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.hpo.etl.HPOGraphExporter;
import de.unibi.agbi.biodwh2.hpo.etl.HPOMappingDescriber;
import de.unibi.agbi.biodwh2.hpo.etl.HPOUpdater;

import java.util.Map;

public class HPOAnnotationsDataSource extends DataSource {
    @Override
    public String getId() {
        return "HPOAnnotations";
    }

    @Override
    public String getLicense() {
        return "HPO license";
    }

    @Override
    public String getLicenseUrl() {
        return "https://hpo.jax.org/app/license";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public Updater<HPOAnnotationsDataSource> getUpdater() {
        return new HPOUpdater(this);
    }

    @Override
    protected Parser<HPOAnnotationsDataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<HPOAnnotationsDataSource> getGraphExporter() {
        return new HPOGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new HPOMappingDescriber(this);
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("omimLicensed", DataSourcePropertyType.BOOLEAN);
        return result;
    }
}
