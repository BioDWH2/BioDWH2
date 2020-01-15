package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "unii", "name", "unit", "quantity", "substanceUnii", "substanceName", "ndcProductCode",
        "structID", "quantityDenomUnit", "quantityDenomValue"
})

public final class ActiveIngredient {
    @JsonProperty("id")
    public String id;
    @JsonProperty("unii")
    public String unii;
    @JsonProperty("name")
    public String name;
    @JsonProperty("unit")
    public String unit;
    @JsonProperty("quantity")
    public String quantity;
    @JsonProperty("substanceUnii")
    public String substanceUnii;
    @JsonProperty("substanceName")
    public String substanceName;
    @JsonProperty("ndcProductCode")
    public String ndcProductCode;
    @JsonProperty("structID")
    public String structId;
    @JsonProperty("quantityDenomUnit")
    public String quantityDenomUnit;
    @JsonProperty("quantityDenomValue")
    public String quantityDenomValue;
}
