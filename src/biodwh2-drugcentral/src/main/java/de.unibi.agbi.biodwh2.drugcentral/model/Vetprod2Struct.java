package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"prodid", "struct_id"})
public class Vetprod2Struct {
    @JsonProperty("prodid")
    public Long prodId;
    @JsonProperty("struct_id")
    public Long structId;
}
