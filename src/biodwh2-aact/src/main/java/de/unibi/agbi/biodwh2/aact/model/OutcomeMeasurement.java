package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id", "nct_id", "outcome_id", "result_group_id", "ctgov_group_code", "classification", "category", "title",
        "description", "units", "param_type", "param_value", "param_value_num", "dispersion_type", "dispersion_value",
        "dispersion_value_num", "dispersion_lower_limit", "dispersion_upper_limit", "explanation_of_na"
})
public class OutcomeMeasurement {
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
    @JsonProperty("classification")
    public String classification;
    @JsonProperty("category")
    public String category;
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
    @JsonProperty("units")
    public String units;
    @JsonProperty("param_type")
    public String paramType;
    @JsonProperty("param_value")
    public String paramValue;
    @JsonProperty("param_value_num")
    public String paramValueNum;
    @JsonProperty("dispersion_type")
    public String dispersionType;
    @JsonProperty("dispersion_value")
    public String dispersionValue;
    @JsonProperty("dispersion_value_num")
    public String dispersionValueNum;
    @JsonProperty("dispersion_lower_limit")
    public String dispersionLowerLimit;
    @JsonProperty("dispersion_upper_limit")
    public String dispersionUpperLimit;
    @JsonProperty("explanation_of_na")
    public String explanationOfNa;
}
