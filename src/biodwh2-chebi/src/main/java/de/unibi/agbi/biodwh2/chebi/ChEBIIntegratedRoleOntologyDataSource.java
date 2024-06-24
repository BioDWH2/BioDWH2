package de.unibi.agbi.biodwh2.chebi;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class ChEBIIntegratedRoleOntologyDataSource extends OBOFoundryOntologyDataSource {
    public ChEBIIntegratedRoleOntologyDataSource() {
        super("ChEBIIntegratedRoleOntology", "chiro.obo", License.CC0_1_0, "ChEBI Integrated Role Ontology (CHIRO)",
              "CHIRO", DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
