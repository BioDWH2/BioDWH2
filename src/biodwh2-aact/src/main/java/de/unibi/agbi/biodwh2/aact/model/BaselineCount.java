package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "result_group_id", "ctgov_group_code", "units", "scope", "count"})
public class BaselineCount {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("result_group_id")
    public Long resultGroupId;
    @JsonProperty("ctgov_group_code")
    public String ctgovGroupCode;
    @JsonProperty("units")
    public String units;
    @JsonProperty("scope")
    public String scope;
    @JsonProperty("count")
    public Integer count;
}
