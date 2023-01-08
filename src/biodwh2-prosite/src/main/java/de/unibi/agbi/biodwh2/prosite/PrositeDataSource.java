package de.unibi.agbi.biodwh2.prosite;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.prosite.etl.PrositeGraphExporter;
import de.unibi.agbi.biodwh2.prosite.etl.PrositeMappingDescriber;
import de.unibi.agbi.biodwh2.prosite.etl.PrositeUpdater;

public class PrositeDataSource extends DataSource {
    @Override
    public String getId() {
        return "PROSITE";
    }

    @Override
    public String getFullName() {
        return "Expasy PROSITE";
    }

    @Override
    public String getLicense() {
        return "CC BY-NC-ND 4.0";
    }

    @Override
    public String getLicenseUrl() {
        return "https://prosite.expasy.org/prosite_license.html";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new PrositeUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new PrositeGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new PrositeMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
