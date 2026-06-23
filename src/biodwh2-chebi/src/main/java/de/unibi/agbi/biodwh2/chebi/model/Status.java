package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name"})
public class Status {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("name")
    public String name;
}
