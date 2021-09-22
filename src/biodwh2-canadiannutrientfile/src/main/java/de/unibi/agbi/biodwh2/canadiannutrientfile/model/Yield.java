package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"YieldID", "YieldDescription", "YieldDescriptionF"})
public class Yield {
    @JsonProperty("YieldID")
    public String id;
    @JsonProperty("YieldDescription")
    public String description;
    @JsonProperty("YieldDescriptionF")
    public String descriptionFrench;
}
