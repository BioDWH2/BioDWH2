package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "result_group_id", "ctgov_group_code", "period", "reason", "count"})
public class DropWithdrawal {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("result_group_id")
    public Long resultGroupId;
    @JsonProperty("ctgov_group_code")
    public String ctgovGroupCode;
    @JsonProperty("period")
    public String period;
    @JsonProperty("reason")
    public String reason;
    @JsonProperty("count")
    public Integer count;
}
