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
    public Updater<MEDRTDataSource> getUpdater() {
        return new MEDRTUpdater();
    }

    @Override
    public Parser<MEDRTDataSource> getParser() {
        return new MEDRTParser();
    }

    @Override
    public RDFExporter<MEDRTDataSource> getRdfExporter() {
        return new EmptyRDFExporter<>();
    }

    @Override
    public GraphExporter<MEDRTDataSource> getGraphExporter() {
        return new MEDRTGraphExporter();
    }

    @Override
    protected void unloadData() {
        terminology = null;
    }
}
