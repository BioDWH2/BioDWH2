package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;

public class DrugBankGraphExporter extends GraphExporter<DrugBankDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final DrugBankDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        return false;
    }
}
