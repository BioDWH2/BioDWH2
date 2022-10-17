package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_mirna", "auto_lit", "comment", "order_added"})
public class MirnaLiteratureReference {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("auto_lit")
    public Long autoLit;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("order_added")
    public Integer orderAdded;
}
