package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "ndcProductCode", "form", "genericName", "productCode",
"route", "marketingStatus", "activeIngredientCount"})

public final class Product {
    @JsonProperty("id")
    public String id;
    @JsonProperty("ndcProductCode")
    public String ndcProductCode;
    @JsonProperty("form")
    public String form;
    @JsonProperty("genericName")
    public String genericName;
    @JsonProperty("productName")
    public String productName;
    @JsonProperty("route")
    public String route;
    @JsonProperty("marketingStatus")
    public String marketingStatus;
    @JsonProperty("activeIngredientCount")
    public String activeIngredientCount;
}
