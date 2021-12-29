package de.unibi.agbi.biodwh2.string.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"#string_protein_id", "preferred_name", "protein_size", "annotation"})
public class ProteinInfo {
    @JsonProperty("#string_protein_id")
    public String proteinId;
    @JsonProperty("preferred_name")
    public String preferredName;
    @JsonProperty("protein_size")
    public Integer proteinSize;
    @JsonProperty("annotation")
    public String annotation;
}
