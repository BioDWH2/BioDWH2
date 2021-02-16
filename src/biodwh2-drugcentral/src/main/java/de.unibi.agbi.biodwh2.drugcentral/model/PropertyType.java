package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"id", "category", "name", "symbol", "units"})
public class PropertyType {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("category")
    public String category;
    @JsonProperty("name")
    public String name;
    @JsonProperty("symbol")
    public String symbol;
    @JsonProperty("units")
    public String units;
}
