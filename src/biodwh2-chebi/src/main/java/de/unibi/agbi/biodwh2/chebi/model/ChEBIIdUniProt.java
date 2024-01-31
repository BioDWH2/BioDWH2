package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"CHEBI_ID", "Name"})
public class ChEBIIdUniProt {
    @JsonProperty("CHEBI_ID")
    public Integer chebiId;
    @JsonProperty("Name")
    public String name;
}
