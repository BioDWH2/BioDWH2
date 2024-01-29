package de.unibi.agbi.biodwh2.reactome;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.text.License;
import de.unibi.agbi.biodwh2.reactome.etl.ReactomeGraphExporter;
import de.unibi.agbi.biodwh2.reactome.etl.ReactomeMappingDescriber;
import de.unibi.agbi.biodwh2.reactome.etl.ReactomeUpdater;

public class ReactomeDataSource extends DataSource {
    @Override
    public String getId() {
        return "Reactome";
    }

    @Override
    public String getLicense() {
        return License.CC_BY_4_0.getName();
    }

    @Override
    public String getLicenseUrl() {
        return "https://reactome.org/license";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new ReactomeUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new ReactomeGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new ReactomeMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
