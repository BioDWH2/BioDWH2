package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "identifier", "id_type", "struct_id", "parent_match"})
@GraphNodeLabel("Identifier")
public final class Identifier {
    @JsonProperty("id")
    public String id;
    @JsonProperty("identifier")
    @GraphProperty("identifier")
    public String identifier;
    @JsonProperty("id_type")
    @GraphProperty("type")
    public String idType;
    @JsonProperty("struct_id")
    public Integer structId;
    @JsonProperty("parent_match")
    @GraphProperty("parent_match")
    public String parentMatch;
}
