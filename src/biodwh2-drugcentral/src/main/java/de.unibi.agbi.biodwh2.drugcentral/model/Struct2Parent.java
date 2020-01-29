package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"struct_id", "parent_id"})

public final class Struct2Parent {
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("parent_id")
    public String parentId;
}
