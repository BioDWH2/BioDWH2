package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "type"})

public final class RefType {
    @JsonProperty("id")
    public String id;
    @JsonProperty("type")
    public String type;
}
