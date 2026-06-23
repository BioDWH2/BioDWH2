package de.unibi.agbi.biodwh2.chebi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "url", "prefix", "description"})
public class Source {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("url")
    public String url;
    @JsonProperty("prefix")
    public String prefix;
    @JsonProperty("description")
    public String description;
}
