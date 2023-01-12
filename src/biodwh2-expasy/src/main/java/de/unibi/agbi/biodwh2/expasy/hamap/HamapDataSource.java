package de.unibi.agbi.biodwh2.expasy.hamap;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.expasy.hamap.etl.HamapGraphExporter;
import de.unibi.agbi.biodwh2.expasy.hamap.etl.HamapMappingDescriber;
import de.unibi.agbi.biodwh2.expasy.hamap.etl.HamapUpdater;

public class HamapDataSource extends DataSource {
    @Override
    public String getId() {
        return "HAMAP";
    }

    @Override
    public String getFullName() {
        return "Expasy HAMAP";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_ND_4_0.getName();
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
        return new HamapUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new HamapGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new HamapMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
