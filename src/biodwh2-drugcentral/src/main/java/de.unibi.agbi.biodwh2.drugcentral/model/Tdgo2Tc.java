package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "goId", "componentId"})

public final class Tdgo2Tc {
    @JsonProperty("id")
    public String id;
    @JsonProperty("goId")
    public String goId;
    @JsonProperty("componentId")
    public String componentId;
}
