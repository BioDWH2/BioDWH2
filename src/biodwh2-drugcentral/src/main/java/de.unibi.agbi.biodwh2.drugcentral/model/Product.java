package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@SuppressWarnings("unused")
@JsonPropertyOrder({
        "id", "ndc_product_code", "form", "generic_name", "product_name", "route", "marketing_status",
        "active_ingredient_count"
})
@NodeLabel("Product")
public final class Product {
    @JsonProperty("id")
    @GraphProperty("id")
    public String id;
    @JsonProperty("ndc_product_code")
    @GraphProperty("ndc_product_code")
    public String ndcProductCode;
    @JsonProperty("form")
    @GraphProperty("form")
    public String form;
    @JsonProperty("generic_name")
    @GraphProperty("generic_name")
    public String genericName;
    @JsonProperty("product_name")
    @GraphProperty("product_name")
    public String productName;
    @JsonProperty("route")
    @GraphProperty("route")
    public String route;
    @JsonProperty("marketing_status")
    @GraphProperty("marketing_status")
    public String marketingStatus;
    @JsonProperty("active_ingredient_count")
    @GraphProperty("active_ingredient_count")
    public String activeIngredientCount;
}
