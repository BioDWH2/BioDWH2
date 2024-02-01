package de.unibi.agbi.biodwh2.rnadisease;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.rnadisease.etl.RNADiseaseGraphExporter;
import de.unibi.agbi.biodwh2.rnadisease.etl.RNADiseaseMappingDescriber;
import de.unibi.agbi.biodwh2.rnadisease.etl.RNADiseaseUpdater;

public class RNADiseaseDataSource extends DataSource {
    @Override
    public String getId() {
        return "RNADisease";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new RNADiseaseUpdater(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new RNADiseaseGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new RNADiseaseMappingDescriber(this);
    }
}
