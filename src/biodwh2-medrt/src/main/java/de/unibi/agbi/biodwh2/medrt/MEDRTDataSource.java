package de.unibi.agbi.biodwh2.medrt;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.medrt.etl.MEDRTGraphExporter;
import de.unibi.agbi.biodwh2.medrt.etl.MEDRTMappingDescriber;
import de.unibi.agbi.biodwh2.medrt.etl.MEDRTParser;
import de.unibi.agbi.biodwh2.medrt.etl.MEDRTUpdater;
import de.unibi.agbi.biodwh2.medrt.model.Terminology;

public class MEDRTDataSource extends DataSource {
    public Terminology terminology;

    @Override
    public String getId() {
        return "MED-RT";
    }

    @Override
    public String getLicense() {
        return "UMLS license";
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
