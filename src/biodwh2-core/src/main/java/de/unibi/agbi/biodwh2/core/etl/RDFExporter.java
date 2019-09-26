package de.unibi.agbi.biodwh2.core.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public abstract class RDFExporter {
    public abstract Model export(DataSource dataSource);

    protected final Model createDefaultModel() {
        return ModelFactory.createDefaultModel();
    }
}
