package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class CompositionalDietaryNutritionOntologyDataSource extends OBOFoundryOntologyDataSource {
	public CompositionalDietaryNutritionOntologyDataSource() {
		super("CompositionalDietaryNutritionOntology", "cdno.obo", License.CC_BY_3_0,
			  "Compositional Dietary Nutrition Ontology", "CDNO", DataVersionFormat.DASHED_YYYY_MM_DD);
	}
}
