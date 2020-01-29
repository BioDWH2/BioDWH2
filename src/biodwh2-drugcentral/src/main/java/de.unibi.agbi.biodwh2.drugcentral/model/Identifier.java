package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "identifier", "id_type", "struct_id", "parent_match"})

public final class Identifier {
    @JsonProperty("id")
    public String id;
    @JsonProperty("identifier")
    public String identifier;
    @JsonProperty("id_type")
    public String idType;
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("parent_match")
    public String parentMatch;
}
