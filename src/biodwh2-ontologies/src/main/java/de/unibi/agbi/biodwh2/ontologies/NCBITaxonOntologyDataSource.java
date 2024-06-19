package de.unibi.agbi.biodwh2.ontologies;

import de.unibi.agbi.biodwh2.core.OBOFoundryOntologyDataSource;
import de.unibi.agbi.biodwh2.core.text.License;

@SuppressWarnings("unused")
public class NCBITaxonOntologyDataSource extends OBOFoundryOntologyDataSource {
    public NCBITaxonOntologyDataSource() {
        super("NCBITaxonOntology", "ncbitaxon.obo", License.CC0_1_0, "NCBITaxon Ontology", "NCBITaxon",
              DataVersionFormat.DASHED_YYYY_MM_DD);
    }
}
