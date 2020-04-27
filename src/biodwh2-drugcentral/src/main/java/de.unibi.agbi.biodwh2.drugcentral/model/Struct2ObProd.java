package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"struct_id", "prod_id", "strength"})

public final class Struct2ObProd {
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("prod_id")
    public String prodId;
    @JsonProperty("strength")
    public String strength;
}