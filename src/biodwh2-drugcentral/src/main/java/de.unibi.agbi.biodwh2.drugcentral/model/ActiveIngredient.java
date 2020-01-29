package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "active_moiety_unii", "active_moiety_name", "unit", "quantity", "substance_unii", "substance_name",
        "ndc_product_code", "struct_id", "quantity_denom_unit", "quantity_denom_value"
})

public final class ActiveIngredient {
    @JsonProperty("id")
    public String id;
    @JsonProperty("active_moiety_unii")
    public String unii;
    @JsonProperty("active_moiety_name")
    public String name;
    @JsonProperty("unit")
    public String unit;
    @JsonProperty("quantity")
    public String quantity;
    @JsonProperty("substance_unii")
    public String substanceUnii;
    @JsonProperty("substance_name")
    public String substanceName;
    @JsonProperty("ndc_product_code")
    public String ndcProductCode;
    @JsonProperty("struct_id")
    public String structId;
    @JsonProperty("quantity_denom_unit")
    public String quantityDenomUnit;
    @JsonProperty("quantity_denom_value")
    public String quantityDenomValue;
}
