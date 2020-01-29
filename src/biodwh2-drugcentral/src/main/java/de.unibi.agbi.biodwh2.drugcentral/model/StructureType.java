package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "struct_id", "type"})

public final class StructureType {
    @JsonProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("type")
    public String type;
}
