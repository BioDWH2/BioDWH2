package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class MedicalActionOntologyDataSource extends OBOFoundryOntologyDataSource {
	public MedicalActionOntologyDataSource() {
		super("MedicalActionOntology", "maxo-base.obo", License.CC_BY_4_0, "Medical Action Ontology (MAxO)", "MAXO",
			  DataVersionFormat.DASHED_YYYY_MM_DD);
	}

	@Override
	public String getDownloadUrl() {
		return "https://purl.obolibrary.org/obo/maxo/" + getTargetFileName();
	}
}
