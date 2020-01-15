package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"id", "term", "type"})

public final class TargetGo {
    @JsonProperty("id")
    public String id;
    @JsonProperty("term")
    public String term;
    @JsonProperty("type")
    public String type;
}
