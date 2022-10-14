package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"struct_id", "prod_id", "strength"})
public final class Struct2ObProd {
    @JsonProperty("struct_id")
    public Long structId;
    @JsonProperty("prod_id")
    public Integer prodId;
    @JsonProperty("strength")
    public String strength;
}
