package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "name", "type"})
@GraphNodeLabel("AttributeType")
public final class AttributeType {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
}
