package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"structure_id", "wurcs"})
public class WURCS {
    @JsonProperty("structure_id")
    public Integer structureId;
    @JsonProperty("wurcs")
    public String wurcs;
}
