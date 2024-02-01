package de.unibi.agbi.biodwh2.hprd;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.hprd.etl.HPRDGraphExporter;
import de.unibi.agbi.biodwh2.hprd.etl.HPRDMappingDescriber;
import de.unibi.agbi.biodwh2.hprd.etl.HPRDUpdater;

public class HPRDDataSource extends DataSource {
    @Override
    public String getId() {
        return "HPRD";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new HPRDUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new HPRDGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new HPRDMappingDescriber(this);
    }
}
