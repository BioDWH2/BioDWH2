package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;

public class DrugBankGraphExporter extends GraphExporter<DrugBankDataSource> {
    @Override
    protected Graph exportGraph(DrugBankDataSource dataSource) {
        return new Graph();
    }
}
