package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "id", "struct_id", "meddra_name", "meddra_code", "level", "llr", "llr_threshold", "drug_ae", "drug_no_ae",
        "no_drug_ae", "no_drug_no_ae"
})
@GraphNodeLabel("FAERS")
public final class Faers {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public Integer structId;
    @JsonProperty("meddra_name")
    @GraphProperty("meddra_name")
    public String meddraName;
    @JsonProperty("meddra_code")
    @GraphProperty("meddra_code")
    public String meddraCode;
    @JsonProperty("level")
    @GraphProperty("level")
    public String level;
    @JsonProperty("llr")
    @GraphProperty("llr")
    public String llr;
    @JsonProperty("llr_threshold")
    @GraphProperty("llr_threshold")
    public String llrThreshold;
    @JsonProperty("drug_ae")
    @GraphProperty("drug_ae")
    public String drugAe;
    @JsonProperty("drug_no_ae")
    @GraphProperty("drug_no_ae")
    public String drugNoAe;
    @JsonProperty("no_drug_ae")
    @GraphProperty("no_drug_ae")
    public String noDrugAe;
    @JsonProperty("no_drug_no_ae")
    @GraphProperty("no_drug_no_ae")
    public String noDrugNoAe;
}
