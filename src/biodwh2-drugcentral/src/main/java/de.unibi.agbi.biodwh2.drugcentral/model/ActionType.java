package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {"id", "action_type", "description", "parent_type"})
@NodeLabels({"ActionType"})
public final class ActionType {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("action_type")
    @GraphProperty("action_type")
    public String actionType;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("parent_type")
    @GraphProperty("parent_type")
    public String parentType;
}
