package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class SymptomOntologyDataSource extends OBOFoundryOntologyDataSource {
    public SymptomOntologyDataSource() {
        super("SymptomOntology", "symp.obo", License.CC0_1_0, "Symptom Ontology (SYMP)", "SYMP",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
