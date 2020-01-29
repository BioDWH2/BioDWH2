package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "name", "target_class", "protein_components", "protein_type", "tdl"})

public final class TargetDictionary {
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("target_class")
    public String targetClass;
    @JsonProperty("protein_components")
    public String proteiComponents;
    @JsonProperty("protein_type")
    public String proteinType;
    @JsonProperty("tdl")
    public String tdl;
}
