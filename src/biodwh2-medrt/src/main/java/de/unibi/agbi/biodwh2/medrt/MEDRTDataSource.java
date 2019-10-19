package de.unibi.agbi.biodwh2.medrt;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.medrt.etl.MEDRTGraphExporter;
import de.unibi.agbi.biodwh2.medrt.etl.MEDRTParser;
import de.unibi.agbi.biodwh2.medrt.etl.MEDRTRDFExporter;
import de.unibi.agbi.biodwh2.medrt.etl.MEDRTUpdater;
import de.unibi.agbi.biodwh2.medrt.model.Terminology;

public class MEDRTDataSource extends DataSource {
    public Terminology terminology;

    @Override
    public String getId() {
        return "MED-RT";
    }

    @Override
    public Updater getUpdater() {
        return new MEDRTUpdater();
    }

    @Override
    public Parser getParser() {
        return new MEDRTParser();
    }

    @Override
    public RDFExporter getRdfExporter() {
        return new MEDRTRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new MEDRTGraphExporter();
    }
}
