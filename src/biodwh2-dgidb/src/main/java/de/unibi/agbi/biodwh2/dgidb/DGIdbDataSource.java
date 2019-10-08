package de.unibi.agbi.biodwh2.dgidb;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbGraphExporter;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbParser;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbRDFExporter;
import de.unibi.agbi.biodwh2.dgidb.etl.DGIdbUpdater;

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
        return new DGIdbRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new DGIdbGraphExporter();
    }
}
