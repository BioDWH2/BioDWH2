package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class BasicFormalOntologyDataSource extends OBOFoundryOntologyDataSource {
    public BasicFormalOntologyDataSource() {
        super("BasicFormalOntology", "bfo.obo", License.CC_BY_4_0, "Basic Formal Ontology (BFO)", "BFO",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
