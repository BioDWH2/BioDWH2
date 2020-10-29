package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "name", "is_group", "source"})
@NodeLabel("DrugClass")
public final class DrugClass {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("is_group")
    @GraphProperty("is_group")
    public String isGroup;
    @JsonProperty("source")
    @GraphProperty("source")
    public String source;
}
