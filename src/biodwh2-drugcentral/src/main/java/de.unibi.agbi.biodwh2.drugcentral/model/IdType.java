package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@JsonPropertyOrder(value = {"id", "type", "description", "url"})
@NodeLabels({"IdType"})
public final class IdType {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
    @JsonProperty("description")
    @GraphProperty("description")
    public String description;
    @JsonProperty("url")
    @GraphProperty("url")
    public String url;
}
