package de.unibi.agbi.biodwh2.sider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "atc"})
public class DrugAtc {
    @JsonProperty("id")
    public String id;
    @JsonProperty("atc")
    public String atc;
}
