package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ID", "COMPOUND_ID", "TYPE", "SOURCE", "NAME", "ADAPTED", "LANGUAGE"})
public class Name {
    @JsonProperty("ID")
    public Integer id;
    @JsonProperty("COMPOUND_ID")
    public Integer compoundId;
    @JsonProperty("TYPE")
    public String type;
    @JsonProperty("SOURCE")
    public String source;
    @JsonProperty("NAME")
    public String name;
    @JsonProperty("ADAPTED")
    public String adapted;
    @JsonProperty("LANGUAGE")
    public String language;
}
