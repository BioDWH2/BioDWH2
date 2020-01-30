package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import org.apache.jena.rdf.model.Model;

public final class EmptyRDFExporter extends RDFExporter {
    @Override
    protected Model exportModel(DataSource dataSource) {
        return createDefaultModel();
    }
}
