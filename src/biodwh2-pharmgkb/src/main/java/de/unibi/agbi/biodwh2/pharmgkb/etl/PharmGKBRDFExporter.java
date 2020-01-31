package de.unibi.agbi.biodwh2.pharmgkb.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import org.apache.jena.rdf.model.Model;

public class PharmGKBRDFExporter extends RDFExporter {
    @Override
    protected Model exportModel(DataSource dataSource) {
        Model model = createDefaultModel();
        return model;
    }
}
