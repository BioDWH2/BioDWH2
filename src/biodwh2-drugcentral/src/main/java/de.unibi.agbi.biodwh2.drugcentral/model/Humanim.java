package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"struct_id", "human", "animal"})
public class Humanim {
    @JsonProperty("struct_id")
    public Long structId;
    @JsonProperty("human")
    public String human;
    @JsonProperty("animal")
    public String animal;
}
