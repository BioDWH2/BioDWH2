package de.unibi.agbi.biodwh2.dgidb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbGraphExporter;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbMappingDescriber;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbUpdater;

public class DGIdbDataSource extends DataSource {
    @Override
    public String getId() {
        return "DGIdb";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<DGIdbDataSource> getUpdater() {
        return new DGIdbUpdater(this);
    }

    @Override
    public GraphExporter<DGIdbDataSource> getGraphExporter() {
        return new DGIdbGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new DGIdbMappingDescriber(this);
    }
}
