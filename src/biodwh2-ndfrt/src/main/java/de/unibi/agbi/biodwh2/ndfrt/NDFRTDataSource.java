package de.unibi.agbi.biodwh2.ndfrt;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.ndfrt.etl.NDFRTGraphExporter;
import de.unibi.agbi.biodwh2.ndfrt.etl.NDFRTMappingDescriber;
import de.unibi.agbi.biodwh2.ndfrt.etl.NDFRTParser;
import de.unibi.agbi.biodwh2.ndfrt.etl.NDFRTUpdater;
import de.unibi.agbi.biodwh2.ndfrt.model.Terminology;

public class NDFRTDataSource extends DataSource {
    public Terminology terminology;

    @Override
    public String getId() {
        return "NDF-RT";
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
    public Updater<NDFRTDataSource> getUpdater() {
        return new NDFRTUpdater(this);
    }

    @Override
    public Parser<NDFRTDataSource> getParser() {
        return new NDFRTParser(this);
    }

    @Override
    public GraphExporter<NDFRTDataSource> getGraphExporter() {
        return new NDFRTGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new NDFRTMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        terminology = null;
    }
}
