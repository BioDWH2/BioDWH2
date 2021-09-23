package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "ctgov_group_code", "result_type", "title", "description"})
public class ResultGroup {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("ctgov_group_code")
    public String ctgovGroupCode;
    @JsonProperty("result_type")
    public String resultType;
    @JsonProperty("title")
    public String title;
    @JsonProperty("description")
    public String description;
}
