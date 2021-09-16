package de.unibi.agbi.biodwh2.drugbank;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.DevelopmentState;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.drugbank.etl.*;
import de.unibi.agbi.biodwh2.drugbank.model.DrugStructure;
import de.unibi.agbi.biodwh2.drugbank.model.MetaboliteStructure;

import java.util.List;

public class DrugBankDataSource extends DataSource {
    public List<DrugStructure> drugStructures;
    public List<MetaboliteStructure> metaboliteStructures;

    @Override
    public String getId() {
        return "DrugBank";
    }

    @Override
    public DevelopmentState getDevelopmentState() {
        return DevelopmentState.InDevelopment;
    }

    @Override
    public Updater<DrugBankDataSource> getUpdater() {
        return new DrugBankUpdater(this);
    }

    @Override
    public Parser<DrugBankDataSource> getParser() {
        return new DrugBankParser(this);
    }

    @Override
    public GraphExporter<DrugBankDataSource> getGraphExporter() {
        return new DrugBankGraphExporter(this);
    }

    @Override
    public MappingDescriber getMappingDescriber() {
        return new DrugBankMappingDescriber(this);
    }

    @Override
    protected void unloadData() {
        drugStructures = null;
        metaboliteStructures = null;
    }
}
