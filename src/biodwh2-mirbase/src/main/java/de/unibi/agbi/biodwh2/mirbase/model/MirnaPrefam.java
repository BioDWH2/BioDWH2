package de.unibi.agbi.biodwh2.mirbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"auto_prefam", "prefam_acc", "prefam_id", "description"})
public class MirnaPrefam {
    @JsonProperty("auto_prefam")
    public Long autoPrefam;
    @JsonProperty("prefam_acc")
    public String prefamAcc;
    @JsonProperty("prefam_id")
    public String prefamId;
    @JsonProperty("description")
    public String description;
}
