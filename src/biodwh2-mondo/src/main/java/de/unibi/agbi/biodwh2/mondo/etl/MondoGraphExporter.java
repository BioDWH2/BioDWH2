package de.unibi.agbi.biodwh2.mondo.etl;

import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.mondo.MondoDataSource;

public class MondoGraphExporter extends OntologyGraphExporter<MondoDataSource> {
    public MondoGraphExporter(final MondoDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected String getOntologyFileName() {
        return "mondo.obo";
    }
}
