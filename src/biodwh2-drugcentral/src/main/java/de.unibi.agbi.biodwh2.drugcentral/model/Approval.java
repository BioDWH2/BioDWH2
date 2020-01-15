package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "structId", "approval", "type", "applicant", "orphan"})

public final class Approval {
    @JsonProperty("id")
    public String id;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("approval")
    public String approval;
    @JsonProperty("type")
    public String type;
    @JsonProperty("applicant")
    public String applicant;
    @JsonProperty("orphan")
    public String orphan;
}
