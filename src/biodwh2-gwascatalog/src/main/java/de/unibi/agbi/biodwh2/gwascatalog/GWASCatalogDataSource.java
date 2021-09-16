package de.unibi.agbi.biodwh2.gwascatalog;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.gwascatalog.etl.GWASCatalogGraphExporter;
import de.unibi.agbi.biodwh2.gwascatalog.etl.GWASCatalogMappingDescriber;
import de.unibi.agbi.biodwh2.gwascatalog.etl.GWASCatalogUpdater;

public class GWASCatalogDataSource extends DataSource {
    @Override
    public String getId() {
        return "GWASCatalog";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new GWASCatalogUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new GWASCatalogGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new GWASCatalogMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
