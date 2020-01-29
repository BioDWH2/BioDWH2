package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "struct_id", "meddra_name", "meddra_code", "level", "llr", "llr_threshold", "drug_ae", "drug_no_ae",
        "no_drug_ae", "no_drug_no_ae"
})

public final class Faers {
    @JsonProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("meddra_name")
    public String meddraName;
    @JsonProperty("meddra_code")
    public String meddraCode;
    @JsonProperty("level")
    public String level;
    @JsonProperty("llr")
    public String llr;
    @JsonProperty("llr_threshold")
    public String llrThreshold;
    @JsonProperty("drug_ae")
    public String drugAe;
    @JsonProperty("drug_no_ae")
    public String drugNoAe;
    @JsonProperty("no_drug_ae")
    public String noDrugAe;
    @JsonProperty("no_drug_no_ae")
    public String NoDrugNoAe;
}
