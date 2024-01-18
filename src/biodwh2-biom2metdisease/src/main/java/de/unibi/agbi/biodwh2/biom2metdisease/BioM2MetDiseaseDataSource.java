package de.unibi.agbi.biodwh2.biom2metdisease;

import de.unibi.agbi.biodwh2.biom2metdisease.etl.BioM2MetDiseaseGraphExporter;
import de.unibi.agbi.biodwh2.biom2metdisease.etl.BioM2MetDiseaseMappingDescriber;
import de.unibi.agbi.biodwh2.biom2metdisease.etl.BioM2MetDiseaseParser;
import de.unibi.agbi.biodwh2.biom2metdisease.etl.BioM2MetDiseaseUpdater;
import de.unibi.agbi.biodwh2.biom2metdisease.model.Associations;
import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;

import java.util.List;

public class BioM2MetDiseaseDataSource extends DataSource {
    public List<Associations> associations;

    @Override
    public String getId() {
        return "BioM2MetDisease";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new BioM2MetDiseaseUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new BioM2MetDiseaseParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new BioM2MetDiseaseGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new BioM2MetDiseaseMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}