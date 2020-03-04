package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import org.apache.jena.rdf.model.Model;

public final class EmptyRDFExporter<D extends DataSource> extends RDFExporter<D> {
    @Override
    protected Model exportModel(D dataSource) {
        return createDefaultModel();
    }
}
