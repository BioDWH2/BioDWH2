package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {
        "id", "ndc_product_code", "form", "generic_name", "product_code", "route", "marketing_status",
        "active_ingredient_count"
})

public final class Product {
    @JsonProperty("id")
    public String id;
    @JsonProperty("ndc_product_code")
    public String ndcProductCode;
    @JsonProperty("form")
    public String form;
    @JsonProperty("generic_name")
    public String genericName;
    @JsonProperty("product_name")
    public String productName;
    @JsonProperty("route")
    public String route;
    @JsonProperty("marketing_status")
    public String marketingStatus;
    @JsonProperty("active_ingredient_count")
    public String activeIngredientCount;
}
