package de.unibi.agbi.biodwh2.sequenceontology.etl;

import de.unibi.agbi.biodwh2.core.etl.OntologyGraphExporter;
import de.unibi.agbi.biodwh2.sequenceontology.SequenceOntologyDataSource;

public class SequenceOntologyGraphExporter extends OntologyGraphExporter<SequenceOntologyDataSource> {
    public SequenceOntologyGraphExporter(final SequenceOntologyDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public long getExportVersion() {
        return 1;
    }

    @Override
    protected String getOntologyFileName() {
        return SequenceOntologyUpdater.FILE_NAME;
    }
}
