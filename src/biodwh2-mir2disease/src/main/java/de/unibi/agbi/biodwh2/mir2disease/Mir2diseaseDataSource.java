package de.unibi.agbi.biodwh2.mir2disease;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.mir2disease.etl.Mir2diseaseGraphExporter;
import de.unibi.agbi.biodwh2.mir2disease.etl.Mir2diseaseMappingDescriber;
import de.unibi.agbi.biodwh2.mir2disease.etl.Mir2diseaseParser;
import de.unibi.agbi.biodwh2.mir2disease.etl.Mir2diseaseUpdater;
import de.unibi.agbi.biodwh2.mir2disease.model.AllEntries;
import de.unibi.agbi.biodwh2.mir2disease.model.Disease;
import de.unibi.agbi.biodwh2.mir2disease.model.MiRNATarget;

import java.util.List;

public class Mir2diseaseDataSource extends DataSource {
    public List<AllEntries> allEntries;
    public List<Disease> disease;
    public List<MiRNATarget> miRNATarget;

    @Override
    public String getId() {
        return "miR2Disease";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.Usable;
    }

    @Override
    protected Updater<? extends DataSource> getUpdater() {
        return new Mir2diseaseUpdater(this);
    }

    @Override
    protected Parser<? extends DataSource> getParser() {
        return new Mir2diseaseParser(this);
    }

    @Override
    protected GraphExporter<? extends DataSource> getGraphExporter() {
        return new Mir2diseaseGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new Mir2diseaseMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
    }
}