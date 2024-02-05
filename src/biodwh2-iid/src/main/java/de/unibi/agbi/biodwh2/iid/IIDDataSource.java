package de.unibi.agbi.biodwh2.iid;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.iid.etl.IIDGraphExporter;
import de.unibi.agbi.biodwh2.iid.etl.IIDMappingDescriber;
import de.unibi.agbi.biodwh2.iid.etl.IIDUpdater;

public class IIDDataSource extends DataSource {
    @Override
    public String getId() {
        return "IID";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new IIDUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new IIDGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new IIDMappingDescriber(this);
    }
}
