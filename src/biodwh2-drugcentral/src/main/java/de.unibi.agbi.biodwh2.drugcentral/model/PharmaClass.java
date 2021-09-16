package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "struct_id", "type", "name", "class_code", "source"})
@GraphNodeLabel("PharmaClass")
public final class PharmaClass {
    @JsonProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public Integer structId;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("class_code")
    @GraphProperty("code")
    public String classCode;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
}
