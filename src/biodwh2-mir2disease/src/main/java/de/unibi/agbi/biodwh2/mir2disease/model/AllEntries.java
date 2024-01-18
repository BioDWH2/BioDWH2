package de.unibi.agbi.biodwh2.mir2disease.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"miRNA", "disease name in original paper", "effect", "sequencing method", "Pub Date", "Reference"})
public class AllEntries {
    @JsonProperty("miRNA")
    public String miRNA;
    @JsonProperty("disease name in original paper")
    public String diseaseNameInOriginalPaper;
    @JsonProperty("effect")
    public String effect;
    @JsonProperty("sequencing method")
    public String sequencingMethod;
    @JsonProperty("Pub Date")
    public String pubDate;
    @JsonProperty("Reference")
    public String reference;
}
