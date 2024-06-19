package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class PathogenTransmissionOntologyDataSource extends OBOFoundryOntologyDataSource {
    public PathogenTransmissionOntologyDataSource() {
        super("PathogenTransmissionOntology", "trans.obo", License.CC0_1_0, "Pathogen Transmission Ontology", "TRANS",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
