package de.unibi.agbi.biodwh2.disgenet;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetGraphExporter;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetParser;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetUpdater;

public class DisGeNetDataSource extends DataSource {
    @Override
    public String getId() {
        return "DisGeNET";
    }

    @Override
    public Updater getUpdater() {
        return new DisGeNetUpdater();
    }

    @Override
    protected Parser getParser() {
        return new DisGeNetParser();
    }

    @Override
    protected RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    protected GraphExporter getGraphExporter() {
        return new DisGeNetGraphExporter();
    }
}
