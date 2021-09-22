package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "FoodID", "FoodCode", "FoodGroupID", "FoodSourceID", "FoodDescription", "FoodDescriptionF", "FoodDateOfEntry",
        "FoodDateOfPublication", "CountryCode", "ScientificName"
})
public class Food {
    @JsonProperty("FoodID")
    public String id;
    @JsonProperty("FoodCode")
    public String code;
    @JsonProperty("FoodGroupID")
    public String foodGroupID;
    @JsonProperty("FoodSourceID")
    public String foodSourceID;
    @JsonProperty("FoodDescription")
    public String description;
    @JsonProperty("FoodDescriptionF")
    public String descriptionFrench;
    @JsonProperty("CountryCode")
    public Integer countryCode;
    @JsonProperty("FoodDateOfEntry")
    public String dateOfEntry;
    @JsonProperty("FoodDateOfPublication")
    public String dateOfPublication;
    @JsonProperty("ScientificName")
    public String scientificName;
}
