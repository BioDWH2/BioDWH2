package de.unibi.agbi.biodwh2.drugbank;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.etl.Parser;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.etl.Updater;
import de.unibi.agbi.biodwh2.drugbank.etl.DrugBankGraphExporter;
import de.unibi.agbi.biodwh2.drugbank.etl.DrugBankParser;
import de.unibi.agbi.biodwh2.drugbank.etl.DrugBankRDFExporter;
import de.unibi.agbi.biodwh2.drugbank.etl.DrugBankUpdater;
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
        return new DrugBankRDFExporter();
    }

    @Override
    public GraphExporter getGraphExporter() {
        return new DrugBankGraphExporter();
    }
}
