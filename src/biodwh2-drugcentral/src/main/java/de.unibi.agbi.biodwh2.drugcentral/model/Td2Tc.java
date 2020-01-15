package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"targetId", "componentId"})

public final class Td2Tc {
    @JsonProperty("targetId")
    public String targetId;
    @JsonProperty("componentId")
    public String componentId;
}
