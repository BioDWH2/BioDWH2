package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"tsn", "tsn_accepted", "update_date"})
public class SynonymLink {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("tsn_accepted")
    public int tsnAccepted;
    @JsonProperty("update_date")
    public String updateDate;
}
