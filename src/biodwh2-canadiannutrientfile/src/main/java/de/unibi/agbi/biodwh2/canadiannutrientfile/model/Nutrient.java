package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "NutrientID", "NutrientCode", "NutrientSymbol", "NutrientUnit", "NutrientName", "NutrientNameF", "Tagname",
        "NutrientDecimals"
})
public class Nutrient {
    @JsonProperty("NutrientID")
    public String id;
    @JsonProperty("NutrientCode")
    public String code;
    @JsonProperty("NutrientSymbol")
    public String symbol;
    @JsonProperty("NutrientUnit")
    public String unit;
    @JsonProperty("NutrientName")
    public String name;
    @JsonProperty("NutrientNameF")
    public String nameFrench;
    @JsonProperty("Tagname")
    public String tagName;
    @JsonProperty("NutrientDecimals")
    public Integer decimals;
}
