package de.unibi.agbi.biodwh2.drugbank.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.RDFExporter;
import org.apache.jena.rdf.model.Model;

public class DrugBankRDFExporter extends RDFExporter {
    @Override
    protected Model exportModel(DataSource dataSource) {
        Model model = createDefaultModel();
        return model;
    }
}
