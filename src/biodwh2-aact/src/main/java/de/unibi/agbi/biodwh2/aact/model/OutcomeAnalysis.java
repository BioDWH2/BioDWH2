package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "nct_id", "outcome_id", "non_inferiority_type", "non_inferiority_description", "param_type",
        "param_value", "dispersion_type", "dispersion_value", "p_value_modifier", "p_value", "ci_n_sides", "ci_percent",
        "ci_lower_limit", "ci_upper_limit", "ci_upper_limit_na_comment", "p_value_description", "method",
        "method_description", "estimate_description", "groups_description", "other_analysis_description"
})
public class OutcomeAnalysis {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("outcome_id")
    public Long outcomeId;
    @JsonProperty("non_inferiority_type")
    public String nonInferiorityType;
    @JsonProperty("non_inferiority_description")
    public String nonInferiorityDescription;
    @JsonProperty("param_type")
    public String paramType;
    @JsonProperty("param_value")
    public String paramValue;
    @JsonProperty("dispersion_type")
    public String dispersionType;
    @JsonProperty("dispersion_value")
    public String dispersionValue;
    @JsonProperty("p_value_modifier")
    public String pValueModifier;
    @JsonProperty("p_value")
    public String pValue;
    @JsonProperty("ci_n_sides")
    public String ciNSides;
    @JsonProperty("ci_percent")
    public String ciPercent;
    @JsonProperty("ci_lower_limit")
    public String ciLowerLimit;
    @JsonProperty("ci_upper_limit")
    public String ciUpperLimit;
    @JsonProperty("ci_upper_limit_na_comment")
    public String ciUpperLimitNaComment;
    @JsonProperty("p_value_description")
    public String pValueDescription;
    @JsonProperty("method")
    public String method;
    @JsonProperty("method_description")
    public String methodDescription;
    @JsonProperty("estimate_description")
    public String estimateDescription;
    @JsonProperty("groups_description")
    public String groupsDescription;
    @JsonProperty("other_analysis_description")
    public String otherAnalysisDescription;
}
