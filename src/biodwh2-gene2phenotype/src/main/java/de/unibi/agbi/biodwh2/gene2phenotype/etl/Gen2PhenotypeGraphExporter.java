package de.unibi.agbi.biodwh2.gene2phenotype.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.gene2phenotype.Gen2PhenotypeDataSource;

public class Gen2PhenotypeGraphExporter extends GraphExporter<Gen2PhenotypeDataSource> {
    public Gen2PhenotypeGraphExporter(Gen2PhenotypeDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(Workspace workspace, Graph graph) throws ExporterException {
        return false;
    }
}
