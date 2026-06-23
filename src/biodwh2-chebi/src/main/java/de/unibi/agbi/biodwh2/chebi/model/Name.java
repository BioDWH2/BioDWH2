package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "compound_id", "name", "type", "status_id", "adapted", "language_code", "ascii_name"})
public class Name {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("compound_id")
    public Integer compoundId;
    @JsonProperty("name")
    public String name;
    @JsonProperty("type")
    public String type;
    @JsonProperty("status_id")
    public Integer statusId;
    @JsonProperty("SOURCE")
    public String source;
    @JsonProperty("adapted")
    public String adapted;
    @JsonProperty("language_code")
    public String languageCode;
    @JsonProperty("ascii_name")
    public String asciiName;
}
