package de.unibi.agbi.biodwh2.hpo;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.hpo.etl.HPOGraphExporter;
import de.unibi.agbi.biodwh2.hpo.etl.HPOParser;
import de.unibi.agbi.biodwh2.hpo.etl.HPOUpdater;

public class HPODataSource extends DataSource {
    @Override
    public String getId() {
        return "HPO";
    }

    @Override
    public Updater getUpdater() {
        return new HPOUpdater();
    }

    @Override
    protected Parser getParser() {
        return new HPOParser();
    }

    @Override
    protected RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    protected GraphExporter getGraphExporter() {
        return new HPOGraphExporter();
    }
}
