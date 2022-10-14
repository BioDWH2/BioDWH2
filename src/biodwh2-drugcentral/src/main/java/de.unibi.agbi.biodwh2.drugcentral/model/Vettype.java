package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"prodid", "type"})
public class Vettype {
    @JsonProperty("prodid")
    public Long prodId;
    @JsonProperty("type")
    public String type;
}
