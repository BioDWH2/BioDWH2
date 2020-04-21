package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;

public class GeneOntologyGraphExporter extends GraphExporter<GeneOntologyDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final GeneOntologyDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        return true;
    }
}
