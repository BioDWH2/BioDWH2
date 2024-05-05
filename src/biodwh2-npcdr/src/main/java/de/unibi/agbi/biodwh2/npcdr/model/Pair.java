package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Combination ID", "NP ID", "Drug ID"})
public class Pair {
    @JsonProperty("Combination ID")
    public String id;
    @JsonProperty("NP ID")
    public String naturalProductId;
    @JsonProperty("Drug ID")
    public String drugId;
}
