package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "action_type", "description", "parent_type"})
@GraphNodeLabel("ActionType")
public final class ActionType {
    @JsonProperty("id")
    public String id;
    @JsonProperty("action_type")
    @GraphProperty("type")
    public String actionType;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("parent_type")
    @GraphProperty("parent_type")
    public String parentType;
}
