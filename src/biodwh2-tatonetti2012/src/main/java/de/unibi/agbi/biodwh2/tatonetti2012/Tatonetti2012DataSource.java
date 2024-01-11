package de.unibi.agbi.biodwh2.tatonetti2012;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.tatonetti2012.etl.Tatonetti2012GraphExporter;
import de.unibi.agbi.biodwh2.tatonetti2012.etl.Tatonetti2012MappingDescriber;
import de.unibi.agbi.biodwh2.tatonetti2012.etl.Tatonetti2012Updater;

public class Tatonetti2012DataSource extends DataSource {
    @Override
    public String getId() {
        return "Tatonetti2012";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new Tatonetti2012Updater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new Tatonetti2012GraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new Tatonetti2012MappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
