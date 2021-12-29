package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#string_protein_id", "alias", "source"})
public class ProteinAlias {
    @JsonProperty("#string_protein_id")
    public String proteinId;
    @JsonProperty("alias")
    public String alias;
    @JsonProperty("source")
    public String source;
}
