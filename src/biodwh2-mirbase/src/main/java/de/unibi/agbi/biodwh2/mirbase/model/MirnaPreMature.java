package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_mirna", "auto_mature", "mature_from", "mature_to"})
public class MirnaPreMature {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("auto_mature")
    public Long autoMature;
    @JsonProperty("mature_from")
    public String matureFrom;
    @JsonProperty("mature_to")
    public String matureTo;
}
