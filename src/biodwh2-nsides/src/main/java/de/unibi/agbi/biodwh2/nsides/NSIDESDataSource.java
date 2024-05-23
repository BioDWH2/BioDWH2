package de.unibi.agbi.biodwh2.nsides;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.nsides.etl.NSIDESGraphExporter;
import de.unibi.agbi.biodwh2.nsides.etl.NSIDESMappingDescriber;
import de.unibi.agbi.biodwh2.nsides.etl.NSIDESUpdater;

public class NSIDESDataSource extends DataSource {
    @Override
    public String getId() {
        return "nSIDES";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new NSIDESUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new NSIDESGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new NSIDESMappingDescriber(this);
    }
}
