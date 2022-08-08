package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"disease_id", "disease_category_id", "comment"})
public class Disease2Category {
    @JsonProperty("disease_id")
    public Long diseaseId;
    @JsonProperty("disease_category_id")
    public Long diseaseCategoryId;
    @JsonProperty("comment")
    public String comment;
}
