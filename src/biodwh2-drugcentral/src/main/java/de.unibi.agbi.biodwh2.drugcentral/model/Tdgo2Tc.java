package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "go_id", "component_id"})
public final class Tdgo2Tc {
    @JsonProperty("id")
    public String id;
    @JsonProperty("go_id")
    public String goId;
    @JsonProperty("component_id")
    public Integer componentId;
}
