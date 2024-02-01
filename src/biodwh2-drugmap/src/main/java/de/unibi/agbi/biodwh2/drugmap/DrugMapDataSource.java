package de.unibi.agbi.biodwh2.drugmap;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.drugmap.etl.DrugMapGraphExporter;
import de.unibi.agbi.biodwh2.drugmap.etl.DrugMapMappingDescriber;
import de.unibi.agbi.biodwh2.drugmap.etl.DrugMapUpdater;

public class DrugMapDataSource extends DataSource {
    @Override
    public String getId() {
        return "DrugMap";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new DrugMapUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new DrugMapGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new DrugMapMappingDescriber(this);
    }
}
