package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"MeasureID","MeasureDescription","MeasureDescriptionF"})
public class Measure {
    @JsonProperty("MeasureID")
    public String id;
    @JsonProperty("MeasureDescription")
    public String description;
    @JsonProperty("MeasureDescriptionF")
    public String descriptionFrench;
}
