package de.unibi.agbi.biodwh2.ttd;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.ttd.etl.TTDGraphExporter;
import de.unibi.agbi.biodwh2.ttd.etl.TTDMappingDescriber;
import de.unibi.agbi.biodwh2.ttd.etl.TTDUpdater;

public class TTDDataSource extends DataSource {
    @Override
    public String getId() {
        return "TTD";
    }

    @Override
    public String getFullName() {
        return "Therapeutic Target Database (TTD)";
    }

    @Override
    public String getDescription() {
        return "The TTD provides information about therapeutic protein and nucleic acid targets,the targeted disease, pathway information and the corresponding drugs directed at each of these targets.";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new TTDUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new TTDGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new TTDMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
