package de.unibi.agbi.biodwh2.geneontology.etl;

import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.geneontology.GeneOntologyDataSource;

public class GeneOntologyGraphExporter extends OntologyGraphExporter<GeneOntologyDataSource> {
    public GeneOntologyGraphExporter(final GeneOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected String getOntologyFileName() {
        return "go.obo";
    }
}
