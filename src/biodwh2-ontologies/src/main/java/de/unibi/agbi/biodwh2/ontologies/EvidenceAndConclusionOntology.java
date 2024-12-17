package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class EvidenceAndConclusionOntology extends OBOFoundryOntologyDataSource {
    public EvidenceAndConclusionOntology() {
        super("EvidenceAndConclusionOntology", "eco.obo", License.CC0_1_0, "Evidence & Conclusion Ontology (ECO)",
              "ECO", DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
