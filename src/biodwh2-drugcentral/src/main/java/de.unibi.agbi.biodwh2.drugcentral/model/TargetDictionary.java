package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "name", "target_class", "protein_components", "protein_type", "tdl"})
@NodeLabels({"TargetDictionary"})
public final class TargetDictionary {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("target_class")
    @GraphProperty("target_class")
    public String targetClass;
    @JsonProperty("protein_components")
    @GraphProperty("protein_components")
    public String proteinComponents;
    @JsonProperty("protein_type")
    @GraphProperty("protein_type")
    public String proteinType;
    @JsonProperty("tdl")
    @GraphProperty("tdl")
    public String tdl;
}
