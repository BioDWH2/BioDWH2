package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"structId", "parentId"})

public final class Struct2Parent {
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("parentId")
    public String parentId;
}
