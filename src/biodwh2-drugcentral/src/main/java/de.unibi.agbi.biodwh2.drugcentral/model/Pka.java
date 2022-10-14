package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "struct_id", "pka_level", "value", "pka_type"})
public final class Pka {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("struct_id")
    public Long structId;
    @JsonProperty("pka_level")
    public String pkaLevel;
    @JsonProperty("value")
    public String value;
    @JsonProperty("pka_type")
    public String pkaType;
}
