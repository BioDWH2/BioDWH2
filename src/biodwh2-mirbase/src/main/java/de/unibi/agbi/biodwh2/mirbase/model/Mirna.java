package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "auto_mirna", "mirna_acc", "mirna_id", "previous_mirna_id", "description", "sequence", "comment",
        "auto_species", "dead_flag"
})
public class Mirna {
    @JsonProperty("auto_mirna")
    public Long autoMirna;
    @JsonProperty("mirna_acc")
    public String mirnaAcc;
    @JsonProperty("mirna_id")
    public String mirnaId;
    @JsonProperty("previous_mirna_id")
    public String previousMirnaId;
    @JsonProperty("description")
    public String description;
    @JsonProperty("sequence")
    public String sequence;
    @JsonProperty("comment")
    public String comment;
    @JsonProperty("auto_species")
    public Long autoSpecies;
    @JsonProperty("dead_flag")
    public Integer deadFlag;
}
