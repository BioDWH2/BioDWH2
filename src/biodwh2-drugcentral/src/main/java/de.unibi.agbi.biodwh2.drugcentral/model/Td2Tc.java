package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"target_id", "component_id"})
public final class Td2Tc {
    @JsonProperty("target_id")
    public String targetId;
    @JsonProperty("component_id")
    public String componentId;
}
