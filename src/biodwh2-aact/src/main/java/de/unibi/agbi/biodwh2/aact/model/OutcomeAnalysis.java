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
    // TODO
}
