package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
@JsonPropertyOrder({"tsn", "completename"})
public class LongName {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("completename")
    public String value;
}
