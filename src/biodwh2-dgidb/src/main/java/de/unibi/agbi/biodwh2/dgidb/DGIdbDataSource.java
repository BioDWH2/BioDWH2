package de.unibi.agbi.biodwh2.dgidb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.dgidb.etl.*;

public class DGIdbDataSource extends DataSource {
    @Override
    public String getId() {
        return "DGIdb";
    }

    @Override
    public Updater getUpdater() {
        return new DGIdbUpdater();
    }

    @Override
    public Parser getParser() {
        return new DGIdbParser();
    }

    @Override
    public RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new DGIdbGraphExporter();
    }
}
