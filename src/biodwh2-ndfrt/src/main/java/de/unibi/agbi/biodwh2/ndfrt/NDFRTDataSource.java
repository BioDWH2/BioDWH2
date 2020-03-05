package de.unibi.agbi.biodwh2.ndfrt;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.ndfrt.etl.*;
import de.unibi.agbi.biodwh2.ndfrt.model.Terminology;

public class NDFRTDataSource extends DataSource {
    public Terminology terminology;

    @Override
    public String getId() {
        return "NDF-RT";
    }

    @Override
    public Updater<NDFRTDataSource> getUpdater() {
        return new NDFRTUpdater();
    }

    @Override
    public Parser<NDFRTDataSource> getParser() {
        return new NDFRTParser();
    }

    @Override
    public RDFExporter<NDFRTDataSource> getRdfExporter() {
        return new EmptyRDFExporter<>();
    }

    @Override
    public GraphExporter<NDFRTDataSource> getGraphExporter() {
        return new NDFRTGraphExporter();
    }
}
