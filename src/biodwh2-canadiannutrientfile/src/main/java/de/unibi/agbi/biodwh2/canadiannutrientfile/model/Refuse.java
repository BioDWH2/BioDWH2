package de.unibi.agbi.biodwh2.canadiannutrientfile.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"RefuseID", "RefuseDescription", "RefuseDescriptionF"})
public class Refuse {
    @JsonProperty("RefuseID")
    public String id;
    @JsonProperty("RefuseDescription")
    public String description;
    @JsonProperty("RefuseDescriptionF")
    public String descriptionFrench;
}
