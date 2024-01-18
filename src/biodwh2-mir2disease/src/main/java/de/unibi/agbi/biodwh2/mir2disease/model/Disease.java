package de.unibi.agbi.biodwh2.mir2disease.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"disease name in original paper", "disease ontology ID"})
public class Disease {
    @JsonProperty("disease ontology ID")
    public String diseaseOntologyID;
    @JsonProperty("disease name in original paper")
    public String diseaseNameInOriginalPaper;
}
