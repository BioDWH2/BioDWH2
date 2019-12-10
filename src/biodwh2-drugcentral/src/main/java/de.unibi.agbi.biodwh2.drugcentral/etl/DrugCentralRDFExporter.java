package de.unibi.agbi.biodwh2.drugcentral.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import org.apache.jena.rdf.model.Model;

public class DrugCentralRDFExporter extends RDFExporter {
    @Override
    protected Model exportModel(DataSource dataSource) throws ExporterException {
        Model model = createDefaultModel();
        return model;
    }
}
