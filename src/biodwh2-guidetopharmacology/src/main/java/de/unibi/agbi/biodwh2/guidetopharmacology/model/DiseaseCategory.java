package de.unibi.agbi.biodwh2.guidetopharmacology.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"disease_category_id", "name", "description"})
public class DiseaseCategory {
    @JsonProperty("disease_category_id")
    public Long diseaseCategoryId;
    @JsonProperty("name")
    public String name;
    @JsonProperty("description")
    public String description;
}
