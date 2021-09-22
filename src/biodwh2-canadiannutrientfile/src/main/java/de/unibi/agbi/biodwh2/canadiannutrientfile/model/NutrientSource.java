package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "NutrientSourceID", "NutrientSourceCode", "NutrientSourceDescription", "NutrientSourc DescriptionF"
})
public class NutrientSource {
    @JsonProperty("NutrientSourceID")
    public String id;
    @JsonProperty("NutrientSourceCode")
    public String code;
    @JsonProperty("NutrientSourceDescription")
    public String description;
    @JsonProperty("NutrientSourc DescriptionF")
    public String descriptionFrench;
}
