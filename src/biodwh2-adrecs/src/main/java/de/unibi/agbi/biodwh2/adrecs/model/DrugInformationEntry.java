package de.unibi.agbi.biodwh2.adrecs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@JsonPropertyOrder({
        "DRUG_ID", "DRUG_NAME", "DRUG_SYNONYMS", "DrugBank_ID", "PubChem_ID", "MESH_ID", "KEGG_ID", "TTD_ID"
})
@GraphNodeLabel("Drug")
public class DrugInformationEntry {
    @JsonProperty("DRUG_ID")
    @GraphProperty("id")
    public String id;
    @JsonProperty("DRUG_NAME")
    @GraphProperty("name")
    public String name;
    @JsonProperty("DRUG_SYNONYMS")
    @GraphArrayProperty(value = "synonyms", arrayDelimiter = " | ", emptyPlaceholder = "Not Available")
    public String synonyms;
    @JsonProperty("DrugBank_ID")
    @GraphProperty(value = "drugbank_id", emptyPlaceholder = "Not Available", ignoreEmpty = true)
    public String drugBankId;
    @JsonProperty("PubChem_ID")
    @GraphProperty(value = "pubchem_id", emptyPlaceholder = "Not Available", ignoreEmpty = true)
    public String pubchemId;
    @JsonProperty("MESH_ID")
    @GraphProperty(value = "mesh_id", emptyPlaceholder = "Not Available", ignoreEmpty = true)
    public String meshId;
    @JsonProperty("KEGG_ID")
    @GraphProperty(value = "kegg_id", emptyPlaceholder = "Not Available", ignoreEmpty = true)
    public String keggId;
    @JsonProperty("TTD_ID")
    @GraphProperty(value = "ttd_id", emptyPlaceholder = "Not Available", ignoreEmpty = true)
    public String ttdId;
}
