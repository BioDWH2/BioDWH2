package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "action_type", "description", "parent_type"})

public final class ActionType {
    @JsonProperty("id")
    public String id;
    @JsonProperty("action_type")
    public String actionType;
    @JsonProperty("description")
    public String description;
    @JsonProperty("parent_type")
    public String parentType;
}
