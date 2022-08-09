package de.unibi.agbi.biodwh2.sider;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.sider.etl.SiderGraphExporter;
import de.unibi.agbi.biodwh2.sider.etl.SiderMappingDescriber;
import de.unibi.agbi.biodwh2.sider.etl.SiderUpdater;

public class SiderDataSource extends DataSource {
    @Override
    public String getId() {
        return "Sider";
    }

    @Override
    public String getLicense() {
        return "CC BY-NC-SA 4.0";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<SiderDataSource> getUpdater() {
        return new SiderUpdater(this);
    }

    @Override
    protected Parser<SiderDataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<SiderDataSource> getGraphExporter() {
        return new SiderGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new SiderMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
