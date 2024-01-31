package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"CHEBI_ID", "InChI"})
public class ChEBIIdIchi {
    @JsonProperty("CHEBI_ID")
    public Integer chebiId;
    @JsonProperty("InChI")
    public String inchi;
}
