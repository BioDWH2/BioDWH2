package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"tsn", "jurisdiction_value", "origin", "update_date"})
public class Jurisdiction {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("jurisdiction_value")
    public String value;
    @JsonProperty("origin")
    public String origin;
    @JsonProperty("update_date")
    public String updateDate;
}
