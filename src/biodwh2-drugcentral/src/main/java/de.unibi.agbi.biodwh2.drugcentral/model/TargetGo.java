package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "term", "type"})
@GraphNodeLabel("GOTerm")
public final class TargetGo {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("term")
    @GraphProperty("term")
    public String term;
    @JsonProperty("type")
    @GraphProperty("type")
    public String type;
}
