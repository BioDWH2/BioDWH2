package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "risk", "ddiRefId"})

public final class DdiRisk {
    @JsonProperty("id")
    public String id;
    @JsonProperty("risk")
    public String risk;
    @JsonProperty("ddiRefId")
    public String ddiRefId;

}
