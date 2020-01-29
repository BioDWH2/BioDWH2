package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "risk", "ddi_ref_id"})

public final class DdiRisk {
    @JsonProperty("id")
    public String id;
    @JsonProperty("risk")
    public String risk;
    @JsonProperty("ddi_ref_id")
    public String ddiRefId;

}
