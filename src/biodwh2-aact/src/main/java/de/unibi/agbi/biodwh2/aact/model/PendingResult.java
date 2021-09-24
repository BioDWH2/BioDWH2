package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "event", "event_date_description", "event_date"})
public class PendingResult {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("event")
    public String event;
    @JsonProperty("event_date_description")
    public String eventDateDescription;
    @JsonProperty("event_date")
    public String eventDate;
}
