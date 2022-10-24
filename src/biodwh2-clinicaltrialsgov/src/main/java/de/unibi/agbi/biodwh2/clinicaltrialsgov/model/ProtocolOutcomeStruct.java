package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProtocolOutcomeStruct {
    @JsonProperty(required = true)
    public String measure;
    @JsonProperty(value = "time_frame")
    public String timeFrame;
    public String description;
}
