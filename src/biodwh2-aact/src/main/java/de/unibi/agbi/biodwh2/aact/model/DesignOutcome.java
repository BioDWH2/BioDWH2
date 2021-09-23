package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "outcome_type", "measure", "time_frame", "population", "description"})
public class DesignOutcome {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("outcome_type")
    public String outcomeType;
    @JsonProperty("measure")
    public String measure;
    @JsonProperty("time_frame")
    public String timeFrame;
    @JsonProperty("population")
    public String population;
    @JsonProperty("description")
    public String description;
}
