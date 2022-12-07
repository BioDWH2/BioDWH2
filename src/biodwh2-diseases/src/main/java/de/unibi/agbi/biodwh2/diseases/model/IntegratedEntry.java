package de.unibi.agbi.biodwh2.diseases.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"gene identifier", "gene name", "disease identifier", "disease name", "confidence score"})
public class IntegratedEntry {
    @JsonProperty("gene identifier")
    public String geneIdentifier;
    @JsonProperty("gene name")
    public String geneName;
    @JsonProperty("disease identifier")
    public String diseaseIdentifier;
    @JsonProperty("disease name")
    public String diseaseName;
    @JsonProperty("confidence score")
    public String confidenceScore;
}
