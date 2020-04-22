package de.unibi.agbi.biodwh2.sider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "name"})
public class DrugName {
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
}
