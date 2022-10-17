package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_mirna", "auto_prefam"})
public class Mirna2Prefam {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("auto_prefam")
    public Long autoPrefam;
}
