package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class RelationOntologyDataSource extends OBOFoundryOntologyDataSource {
    public RelationOntologyDataSource() {
        super("RelationOntology", "ro.obo", License.CC0_1_0, "Relation Ontology (RO)", "RO",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
