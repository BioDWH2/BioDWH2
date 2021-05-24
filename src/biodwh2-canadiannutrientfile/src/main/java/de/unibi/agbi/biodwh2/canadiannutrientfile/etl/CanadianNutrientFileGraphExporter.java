package de.unibi.agbi.biodwh2.canadiannutrientfile.etl;

import de.unibi.agbi.biodwh2.canadiannutrientfile.CanadianNutrientFileDataSource;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;

public class CanadianNutrientFileGraphExporter extends GraphExporter<CanadianNutrientFileDataSource> {
    public CanadianNutrientFileGraphExporter(CanadianNutrientFileDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        return false;
    }
}
