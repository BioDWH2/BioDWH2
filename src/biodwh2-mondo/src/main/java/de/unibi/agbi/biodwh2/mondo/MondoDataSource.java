package de.unibi.agbi.biodwh2.mondo;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.mondo.etl.MondoGraphExporter;
import de.unibi.agbi.biodwh2.mondo.etl.MondoParser;
import de.unibi.agbi.biodwh2.mondo.etl.MondoUpdater;

public class MondoDataSource extends DataSource {
    @Override
    public String getId() {
        return "Mondo";
    }

    @Override
    public Updater getUpdater() {
        return new MondoUpdater();
    }

    @Override
    protected Parser getParser() {
        return new MondoParser();
    }

    @Override
    protected RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    protected GraphExporter getGraphExporter() {
        return new MondoGraphExporter();
    }

    @Override
    protected void unloadData() {
    }
}
