package de.unibi.agbi.biodwh2.ptmd;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.ptmd.etl.PTMDGraphExporter;
import de.unibi.agbi.biodwh2.ptmd.etl.PTMDMappingDescriber;
import de.unibi.agbi.biodwh2.ptmd.etl.PTMDUpdater;

public class PTMDDataSource extends DataSource {
    @Override
    public String getId() {
        return "PTMD";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new PTMDUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new PTMDGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new PTMDMappingDescriber(this);
    }
}
