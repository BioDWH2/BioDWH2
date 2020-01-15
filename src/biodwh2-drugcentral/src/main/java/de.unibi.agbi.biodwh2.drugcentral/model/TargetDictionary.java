package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "name", "targetClass", "proteinComponents", "proteinType", "tdl"})

public final class TargetDictionary {
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("targetClass")
    public String targetClass;
    @JsonProperty("proteinComponents")
    public String proteiComponents;
    @JsonProperty("proteinType")
    public String proteinType;
    @JsonProperty("tdl")
    public String tdl;
}
