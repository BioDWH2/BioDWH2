package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "result_group_id", "ctgov_group_code", "title", "period", "description", "count"})
public class Milestone {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("result_group_id")
    public Long resultGroupId;
    @JsonProperty("ctgov_group_code")
    public String ctgovGroupCode;
    @JsonProperty("title")
    public String title;
    @JsonProperty("period")
    public String period;
    @JsonProperty("description")
    public String description;
    @JsonProperty("count")
    public Integer count;
}
