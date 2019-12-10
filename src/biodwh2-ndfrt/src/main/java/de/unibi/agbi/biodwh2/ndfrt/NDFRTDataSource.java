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

    @Override
    public Merger getMerger() {
        return new NDFRTMerger();
    }
}
