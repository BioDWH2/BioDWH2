package de.unibi.agbi.biodwh2.iig;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.iig.etl.IIGGraphExporter;
import de.unibi.agbi.biodwh2.iig.etl.IIGMappingDescriber;
import de.unibi.agbi.biodwh2.iig.etl.IIGUpdater;

public class IIGDataSource extends DataSource {
    @Override
    public String getId() {
        return "IIG";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new IIGUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new IIGGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new IIGMappingDescriber(this);
    }
}
