package de.unibi.agbi.biodwh2.medrt;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.medrt.etl.*;
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

    @Override
    public Merger getMerger() {
        return new MEDRTMerger();
    }
}
