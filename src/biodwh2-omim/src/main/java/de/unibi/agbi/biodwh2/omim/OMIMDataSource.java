package de.unibi.agbi.biodwh2.omim;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DataSourcePropertyType;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.omim.etl.OMIMGraphExporter;
import de.unibi.agbi.biodwh2.omim.etl.OMIMMappingDescriber;
import de.unibi.agbi.biodwh2.omim.etl.OMIMUpdater;

import java.util.Map;

public class OMIMDataSource extends DataSource {
    @Override
    public String getId() {
        return "OMIM";
    }

    @Override
    public String getFullName() {
        return "Online Mendelian Inheritance in Man";
    }

    @Override
    public String getDescription() {
        return "OMIM is a comprehensive, authoritative compendium of human genes and genetic phenotypes.";
    }

    @Override
    public Map<String, DataSourcePropertyType> getAvailableProperties() {
        final Map<String, DataSourcePropertyType> result = super.getAvailableProperties();
        result.put("downloadKey", DataSourcePropertyType.STRING);
        return result;
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<OMIMDataSource> getUpdater() {
        return new OMIMUpdater(this);
    }

    @Override
    public Parser<OMIMDataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    public GraphExporter<OMIMDataSource> getGraphExporter() {
        return new OMIMGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new OMIMMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
