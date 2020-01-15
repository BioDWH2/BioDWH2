package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"structId", "prodId", "strength"})

public final class Struct2ObProd {
    @JsonProperty("structId")
    public String structId;
    @JsonProperty("prodId")
    public String prodId;
    @JsonProperty("strength")
    public String strength;
}
