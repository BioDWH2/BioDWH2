package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.drugbank.DrugBankDataSource;
import de.unibi.agbi.biodwh2.drugbank.model.DrugStructure;
import de.unibi.agbi.biodwh2.drugbank.model.MetaboliteStructure;

import java.util.List;

public class DrugBankGraphExporter extends GraphExporter<DrugBankDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final DrugBankDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        graph.setIndexColumnNames("id");
        exportDrugStructures(graph, dataSource.drugStructures);
        exportMetaboliteStructures(graph, dataSource.metaboliteStructures);
        return true;
    }

    private void exportDrugStructures(final Graph graph,
                                      final List<DrugStructure> drugStructures) throws ExporterException {
        for (DrugStructure drug : drugStructures)
            exportDrugStructure(graph, drug);
    }

    private void exportDrugStructure(final Graph graph, final DrugStructure drug) throws ExporterException {
        createNodeFromModel(graph, drug);
    }

    private void exportMetaboliteStructures(final Graph graph, final List<MetaboliteStructure> metabolites) {
        for (MetaboliteStructure metabolite : metabolites)
            exportMetaboliteStructure(graph, metabolite);
    }

    private void exportMetaboliteStructure(final Graph graph, final MetaboliteStructure metabolite) {
    }
}
