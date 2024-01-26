package de.unibi.agbi.biodwh2.rnainter;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.rnainter.etl.RNAInterGraphExporter;
import de.unibi.agbi.biodwh2.rnainter.etl.RNAInterMappingDescriber;
import de.unibi.agbi.biodwh2.rnainter.etl.RNAInterUpdater;

public class RNAInterDataSource extends DataSource {
    @Override
    public String getId() {
        return "RNAInter";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new RNAInterUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new RNAInterGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new RNAInterMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
