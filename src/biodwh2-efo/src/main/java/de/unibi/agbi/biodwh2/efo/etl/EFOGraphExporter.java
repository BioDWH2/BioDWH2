package de.unibi.agbi.biodwh2.efo.etl;

import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.efo.EFODataSource;

public class EFOGraphExporter extends OntologyGraphExporter<EFODataSource> {
    public EFOGraphExporter(final EFODataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected String getOntologyFileName() {
        return "efo.obo";
    }
}
