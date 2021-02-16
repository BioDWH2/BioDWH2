package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "type", "description"})
public final class StructTypeDef {
    @JsonProperty("id")
    public String id;
    @JsonProperty("type")
    public String type;
    @JsonProperty("description")
    public String description;
}
