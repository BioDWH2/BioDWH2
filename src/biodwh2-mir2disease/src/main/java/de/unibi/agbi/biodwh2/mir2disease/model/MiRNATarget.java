package de.unibi.agbi.biodwh2.mir2disease.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"miRNA", "Validated target", "Pub Date", "Reference"})
public class MiRNATarget {
    @JsonProperty("miRNA")
    public String miRNA;
    @JsonProperty("Validated target")
    public String validatedTarget;
    @JsonProperty("Pub Date")
    public String pubDate;
    @JsonProperty("Reference")
    public String reference;
}
