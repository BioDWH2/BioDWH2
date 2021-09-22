package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "FoodID", "NutrientID", "NutrientValue", "StandardError", "NumberofObservations", "NutrientSourceID",
        "NutrientDateOfEntry"
})
public class NutrientAmount {
    @JsonProperty("FoodID")
    public String foodID;
    @JsonProperty("NutrientID")
    public String nutrientID;
    @JsonProperty("NutrientValue")
    public String value;
    @JsonProperty("StandardError")
    public String standardError;
    @JsonProperty("NumberofObservations")
    public Integer numberOfObservations;
    @JsonProperty("NutrientSourceID")
    public String nutrientSourceID;
    @JsonProperty("NutrientDateOfEntry")
    public String dateOfEntry;
}
