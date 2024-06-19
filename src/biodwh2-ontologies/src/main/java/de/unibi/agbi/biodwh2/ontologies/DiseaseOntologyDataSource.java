package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class DiseaseOntologyDataSource extends OBOFoundryOntologyDataSource {
    public DiseaseOntologyDataSource() {
        super("DiseaseOntology", "doid.obo", License.CC0_1_0, "Disease Ontology (DO)", "DOID",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
