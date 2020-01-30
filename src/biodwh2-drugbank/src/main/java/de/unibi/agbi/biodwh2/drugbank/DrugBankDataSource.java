package de.unibi.agbi.biodwh2.drugbank;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.*;
import de.unibi.agbi.biodwh2.drugbank.etl.*;
import de.unibi.agbi.biodwh2.drugbank.model.Drugbank;

public class DrugBankDataSource extends DataSource {
    public Drugbank drugBankData;

    @Override
    public String getId() {
        return "DrugBank";
    }

    @Override
    public Updater getUpdater() {
        return new DrugBankUpdater();
    }

    @Override
    public Parser getParser() {
        return new DrugBankParser();
    }

    @Override
    public RDFExporter getRdfExporter() {
        return new EmptyRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new DrugBankGraphExporter();
    }

    @Override
    public Merger getMerger() {
        return new DrugBankMerger();
    }
}
