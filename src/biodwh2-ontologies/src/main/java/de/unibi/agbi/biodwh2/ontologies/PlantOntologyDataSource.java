package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class PlantOntologyDataSource extends OBOFoundryOntologyDataSource {
    public PlantOntologyDataSource() {
        super("PlantOntology", "po.obo", License.CC_BY_4_0, "Plant Ontology (PO)", "PO",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
