package de.unibi.agbi.biodwh2.ncbi;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.ncbi.etl.NCBIGraphExporter;
import de.unibi.agbi.biodwh2.ncbi.etl.NCBIMappingDescriber;
import de.unibi.agbi.biodwh2.ncbi.etl.NCBIParser;
import de.unibi.agbi.biodwh2.ncbi.etl.NCBIUpdater;

public class NCBIDataSource extends DataSource {
    @Override
    public String getId() {
        return "NCBI";
    }

    @Override
    public Updater getUpdater() {
        return new NCBIUpdater();
    }

    @Override
    protected Parser getParser() {
        return new NCBIParser();
    }

    @Override
    protected GraphExporter getGraphExporter() {
        return new NCBIGraphExporter();
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new NCBIMappingDescriber();
    }

    @Override
    protected void unloadData() {
    }
}
