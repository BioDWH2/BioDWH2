package de.unibi.agbi.biodwh2.drugbank;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.drugbank.etl.*;
import de.unibi.agbi.biodwh2.drugbank.model.Drugbank;
import de.unibi.agbi.biodwh2.drugbank.model.Metabolite;

import java.util.List;

public class DrugBankDataSource extends DataSource {
    public Drugbank drugBankData;
    public List<Metabolite> metabolites;

    @Override
    public String getId() {
        return "DrugBank";
    }

    @Override
    public Updater<DrugBankDataSource> getUpdater() {
        return new DrugBankUpdater();
    }

    @Override
    public Parser<DrugBankDataSource> getParser() {
        return new DrugBankParser();
    }

    @Override
    public RDFExporter<DrugBankDataSource> getRdfExporter() {
        return new EmptyRDFExporter<>();
    }

    @Override
    public GraphExporter<DrugBankDataSource> getGraphExporter() {
        return new DrugBankGraphExporter();
    }

    @Override
    protected void unloadData() {
        drugBankData = null;
        metabolites = null;
    }
}
