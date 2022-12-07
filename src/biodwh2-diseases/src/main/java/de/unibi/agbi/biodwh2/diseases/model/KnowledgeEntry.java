package de.unibi.agbi.biodwh2.diseases.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "gene identifier", "gene name", "disease identifier", "disease name", "source database", "evidence type",
        "confidence score"
})
public class KnowledgeEntry {
    @JsonProperty("gene identifier")
    public String geneIdentifier;
    @JsonProperty("gene name")
    public String geneName;
    @JsonProperty("disease identifier")
    public String diseaseIdentifier;
    @JsonProperty("disease name")
    public String diseaseName;
    @JsonProperty("source database")
    public String sourceDatabase;
    @JsonProperty("evidence type")
    public String evidenceType;
    @JsonProperty("confidence score")
    public String confidenceScore;
}
