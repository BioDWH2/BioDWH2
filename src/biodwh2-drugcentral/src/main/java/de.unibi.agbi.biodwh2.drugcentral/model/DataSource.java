package de.unibi.agbi.biodwh2.drugcentral.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = {"src_id", "source_name"})

public final class DataSource {
    @JsonProperty("src_id")
    public String srcId;
    @JsonProperty("source_name")
    public String sourceName;
}
