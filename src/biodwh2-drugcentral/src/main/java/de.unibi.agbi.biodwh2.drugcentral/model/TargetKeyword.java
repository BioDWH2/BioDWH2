package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "descr", "category", "keyword"})
@NodeLabel("TargetKeyword")
public final class TargetKeyword {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("descr")
    @GraphProperty("description")
    public String description;
    @JsonProperty("category")
    @GraphProperty("category")
    public String category;
    @JsonProperty("keyword")
    @GraphProperty("keyword")
    public String keyword;
}
