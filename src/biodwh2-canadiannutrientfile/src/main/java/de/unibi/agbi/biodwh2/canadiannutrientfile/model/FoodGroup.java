package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"FoodGroupID", "FoodGroupCode", "FoodGroupName", "FoodGroupNameF"})
public class FoodGroup {
    @JsonProperty("FoodGroupID")
    public String id;
    @JsonProperty("FoodGroupCode")
    public String code;
    @JsonProperty("FoodGroupName")
    public String name;
    @JsonProperty("FoodGroupNameF")
    public String nameFrench;
}
