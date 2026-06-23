package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"compound_id", "secondary_id"})
public class SecondaryID {
    @JsonProperty("compound_id")
    public Integer compoundId;
    @JsonProperty("secondary_id")
    public Integer secondaryId;
}
