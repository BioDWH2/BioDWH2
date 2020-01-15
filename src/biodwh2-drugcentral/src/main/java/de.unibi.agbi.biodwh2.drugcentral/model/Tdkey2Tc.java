package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "tdkeyId", "componentId"})

public final class Tdkey2Tc {
    @JsonProperty("id")
    public String id;
    @JsonProperty("tdkeyId")
    public String tdKeyId;
    @JsonProperty("componentId")
    public String componentID;
}
