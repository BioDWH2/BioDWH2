package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

public class HPOGraphExporter extends OntologyGraphExporter<HPODataSource> {
    public HPOGraphExporter(final HPODataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected String getOntologyFileName() {
        return "hp.obo";
    }
}
