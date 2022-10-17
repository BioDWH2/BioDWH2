package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_mirna", "confidence"})
public class ConfidenceScore {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("confidence")
    public Integer confidence;
}
