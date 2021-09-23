package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "outcome_analysis_id", "result_group_id", "ctgov_group_code"})
public class OutcomeAnalysisGroup {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("outcome_analysis_id")
    public Long outcomeAnalysisId;
    @JsonProperty("result_group_id")
    public Long resultGroupId;
    @JsonProperty("ctgov_group_code")
    public String ctgovGroupCode;
}
