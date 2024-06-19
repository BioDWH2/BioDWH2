package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class EnvironmentOntologyDataSource extends OBOFoundryOntologyDataSource {
    public EnvironmentOntologyDataSource() {
        super("EnvironmentOntology", "envo.obo", License.CC0_1_0, "Environment Ontology (ENVO)", "ENVO",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
