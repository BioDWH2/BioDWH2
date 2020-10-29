package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"nodc_id", "update_date", "tsn"})
public class NodcId {
    @JsonProperty("nodc_id")
    public String id;
    @JsonProperty("update_date")
    public String updateDate;
    @JsonProperty("tsn")
    public int tsn;
}
