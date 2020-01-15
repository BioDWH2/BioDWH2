package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"srcId", "sourceName"})

public final class DataSource {
    @JsonProperty("srcId")
    public String srcId;
    @JsonProperty("sourceName")
    public String sourceName;
}
