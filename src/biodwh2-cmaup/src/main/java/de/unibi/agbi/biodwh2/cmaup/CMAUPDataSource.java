package de.unibi.agbi.biodwh2.cmaup;

import de.unibi.agbi.biodwh2.cmaup.etl.CMAUPGraphExporter;
import de.unibi.agbi.biodwh2.cmaup.etl.CMAUPMappingDescriber;
import de.unibi.agbi.biodwh2.cmaup.etl.CMAUPUpdater;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;

public class CMAUPDataSource extends DataSource {
    @Override
    public String getId() {
        return "CMAUP";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new CMAUPUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new CMAUPGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new CMAUPMappingDescriber(this);
    }
}
