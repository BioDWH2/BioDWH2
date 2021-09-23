package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "design_group_id", "intervention_id"})
public class DesignGroupIntervention {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("design_group_id")
    public Long design_groupId;
    @JsonProperty("intervention_id")
    public Long interventionId;
}
