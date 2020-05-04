package de.unibi.agbi.biodwh2.core.mocks.mock2;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.core.mocks.mock2.etl.*;

public class Mock2DataSource extends DataSource {
    @Override
    public String getId() {
        return "Mock2";
    }

    @Override
    public Updater<Mock2DataSource> getUpdater() {
        return new Mock2Updater();
    }

    @Override
    public Parser<Mock2DataSource> getParser() {
        return new Mock2Parser();
    }

    @Override
    public RDFExporter<Mock2DataSource> getRdfExporter() {
        return new EmptyRDFExporter<>();
    }

    @Override
    public GraphExporter<Mock2DataSource> getGraphExporter() {
        return new Mock2GraphExporter();
    }

    @Override
    protected void unloadData() {
    }
}
