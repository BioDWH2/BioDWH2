package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"tsn", "geographic_value", "update_date"})
public class GeographicDiv {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("geographic_value")
    public String value;
    @JsonProperty("update_date")
    public String updateDate;
}
