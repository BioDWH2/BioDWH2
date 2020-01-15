package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "structId", "pkaLevel", "value", "pkaType"})

public final class Pka {
    @JsonProperty("id")
    public String id;
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("pkaLevel")
    public String pkaLevel;
    @JsonProperty("value")
    public String value;
    @JsonProperty("pkaType")
    public String pkaType;
}
