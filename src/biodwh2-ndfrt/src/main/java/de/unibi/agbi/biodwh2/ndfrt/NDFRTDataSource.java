package de.unibi.agbi.biodwh2.ndfrt;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.ndfrt.etl.NDFRTGraphExporter;
import de.unibi.agbi.biodwh2.ndfrt.etl.NDFRTParser;
import de.unibi.agbi.biodwh2.ndfrt.etl.NDFRTRDFExporter;
import de.unibi.agbi.biodwh2.ndfrt.etl.NDFRTUpdater;
import de.unibi.agbi.biodwh2.ndfrt.model.Terminology;

public class NDFRTDataSource extends DataSource {
    public Terminology terminology;

    @Override
    public String getId() {
        return "NDF-RT";
    }

    @Override
    public Updater getUpdater() {
        return new NDFRTUpdater();
    }

    @Override
    public Parser getParser() {
        return new NDFRTParser();
    }

    @Override
    public RDFExporter getRdfExporter() {
        return new NDFRTRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new NDFRTGraphExporter();
    }
}
