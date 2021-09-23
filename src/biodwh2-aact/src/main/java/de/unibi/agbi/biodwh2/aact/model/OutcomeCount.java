package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "outcome_id", "result_group_id", "ctgov_group_code", "scope", "units", "count"})
public class OutcomeCount {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("outcome_id")
    public Long outcomeId;
    @JsonProperty("result_group_id")
    public Long resultGroupId;
    @JsonProperty("ctgov_group_code")
    public String ctgovGroupCode;
    @JsonProperty("scope")
    public String scope;
    @JsonProperty("units")
    public String units;
    @JsonProperty("count")
    public Integer count;
}
