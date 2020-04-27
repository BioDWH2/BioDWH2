package de.unibi.agbi.biodwh2.sider;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.sider.etl.SiderGraphExporter;
import de.unibi.agbi.biodwh2.sider.etl.SiderParser;
import de.unibi.agbi.biodwh2.sider.etl.SiderUpdater;

public class SiderDataSource extends DataSource {
    @Override
    public String getId() {
        return "Sider";
    }

    @Override
    public Updater getUpdater() {
        return new SiderUpdater();
    }

    @Override
    protected Parser getParser() {
        return new SiderParser();
    }

    @Override
    protected RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    protected GraphExporter getGraphExporter() {
        return new SiderGraphExporter();
    }
}