package de.unibi.agbi.biodwh2.hpo;

import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.OntologyDataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.hpo.etl.HPOGraphExporter;
import de.unibi.agbi.biodwh2.hpo.etl.HPOMappingDescriber;
import de.unibi.agbi.biodwh2.hpo.etl.HPOUpdater;

import java.util.Map;

public class HPODataSource extends OntologyDataSource {
    @Override
    public String getId() {
        return "HPO";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public Updater<HPODataSource> getUpdater() {
        return new HPOUpdater(this);
    }

    @Override
    protected Parser<HPODataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<HPODataSource> getGraphExporter() {
        return new HPOGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new HPOMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("omimLicensed", DataSourcePropertyType.BOOLEAN);
        return result;
    }
}
