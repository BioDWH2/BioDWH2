package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "struct_id", "pka_level", "value", "pka_type"})

public final class Pka {
    @JsonProperty("id")
    public String id;
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("pka_level")
    public String pkaLevel;
    @JsonProperty("value")
    public String value;
    @JsonProperty("pka_type")
    public String pkaType;
}
