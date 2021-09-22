package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"FoodSourceID", "FoodSourceCode", "FoodSourceDescription", "FoodSourceDescriptionF"})
public class FoodSource {
    @JsonProperty("FoodSourceID")
    public String id;
    @JsonProperty("FoodSourceCode")
    public String code;
    @JsonProperty("FoodSourceDescription")
    public String description;
    @JsonProperty("FoodSourceDescriptionF")
    public String descriptionFrench;
}
