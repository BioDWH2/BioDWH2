package de.unibi.agbi.biodwh2.mondo;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.mondo.etl.MONDOGraphExporter;
import de.unibi.agbi.biodwh2.mondo.etl.MONDOParser;
import de.unibi.agbi.biodwh2.mondo.etl.MONDOUpdater;

public class MONDODataSource extends DataSource {
    @Override
    public String getId() {
        return "MONDO";
    }

    @Override
    public Updater getUpdater() {
        return new MONDOUpdater();
    }

    @Override
    protected Parser getParser() {
        return new MONDOParser();
    }

    @Override
    protected RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    protected GraphExporter getGraphExporter() {
        return new MONDOGraphExporter();
    }
}
