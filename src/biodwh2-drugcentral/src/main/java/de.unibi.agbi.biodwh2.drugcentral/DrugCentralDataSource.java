package de.unibi.agbi.biodwh2.drugcentral;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.drugcentral.etl.*;

public class DrugCentralDataSource extends DataSource {
    @Override
    public String getId() {
        return "DrugCentral";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<DrugCentralDataSource> getUpdater() {
        return new DrugCentralUpdater(this);
    }

    @Override
    public Parser<DrugCentralDataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    public GraphExporter<DrugCentralDataSource> getGraphExporter() {
        return new DrugCentralGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new DrugCentralMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
