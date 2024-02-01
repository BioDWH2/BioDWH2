package de.unibi.agbi.biodwh2.rnalocate;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.rnalocate.etl.RNALocateGraphExporter;
import de.unibi.agbi.biodwh2.rnalocate.etl.RNALocateMappingDescriber;
import de.unibi.agbi.biodwh2.rnalocate.etl.RNALocateUpdater;

public class RNALocateDataSource extends DataSource {
    @Override
    public String getId() {
        return "RNALocate";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new RNALocateUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new RNALocateGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new RNALocateMappingDescriber(this);
    }
}
