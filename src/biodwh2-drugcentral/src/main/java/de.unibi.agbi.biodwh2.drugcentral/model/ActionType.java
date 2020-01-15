package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "actionType", "description", "parentType"})

public final class ActionType {
    @JsonProperty("id")
    public String id;
    @JsonProperty("actionType")
    public String actionType;
    @JsonProperty("description")
    public String description;
    @JsonProperty("parentType")
    public String parentType;
}
