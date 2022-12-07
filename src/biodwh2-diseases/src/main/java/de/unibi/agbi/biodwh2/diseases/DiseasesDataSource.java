package de.unibi.agbi.biodwh2.diseases;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.diseases.etl.DiseasesGraphExporter;
import de.unibi.agbi.biodwh2.diseases.etl.DiseasesMappingDescriber;
import de.unibi.agbi.biodwh2.diseases.etl.DiseasesUpdater;

public class DiseasesDataSource extends DataSource {
    @Override
    public String getId() {
        return "DISEASES";
    }

    @Override
    public String getLicense() {
        return "CC BY 4.0";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new DiseasesUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new PassThroughParser<>(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new DiseasesGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new DiseasesMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}
