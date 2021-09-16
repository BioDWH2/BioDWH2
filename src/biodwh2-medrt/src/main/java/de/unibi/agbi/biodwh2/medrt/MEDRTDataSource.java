package de.unibi.agbi.biodwh2.medrt;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
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
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    public Updater<MEDRTDataSource> getUpdater() {
        return new MEDRTUpdater(this);
    }

    @Override
    public Parser<MEDRTDataSource> getParser() {
        return new MEDRTParser(this);
    }

    @Override
    public GraphExporter<MEDRTDataSource> getGraphExporter() {
        return new MEDRTGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new MEDRTMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        terminology = null;
    }
}
