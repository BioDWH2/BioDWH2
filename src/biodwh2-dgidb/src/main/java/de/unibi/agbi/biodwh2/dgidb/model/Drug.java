package de.unibi.agbi.biodwh2.dgidb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "drug_claim_name", "nomenclature", "concept_id", "drug_name", "approved", "immunotherapy", "anti_neoplastic",
        "source_db_name", "source_db_version"
})
public class Drug {
    @JsonProperty("drug_claim_name")
    public String drugClaimName;
    @JsonProperty("nomenclature")
    public String nomenclature;
    @JsonProperty("concept_id")
    public String conceptId;
    @JsonProperty("drug_name")
    public String drugName;
    @JsonProperty("approved")
    public String approved;
    @JsonProperty("immunotherapy")
    public String immunotherapy;
    @JsonProperty("anti_neoplastic")
    public String antiNeoplastic;
    @JsonProperty("source_db_name")
    public String sourceDBName;
    @JsonProperty("source_db_version")
    public String sourceDBVersion;
}
