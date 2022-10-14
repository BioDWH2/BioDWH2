package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "appl_type", "description"})
public class VetprodType {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("appl_type")
    public String applType;
    @JsonProperty("description")
    public String description;
}
