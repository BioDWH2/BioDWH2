package de.unibi.agbi.biodwh2.ttd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"TargetID", "DrugID", "Highest_status", "MOA"})
public class DrugTargetMapping {
    @JsonProperty("TargetID")
    public String targetId;
    @JsonProperty("DrugID")
    public String drugId;
    @JsonProperty("Highest_status")
    public String highestStatus;
    @JsonProperty("MOA")
    public String moa;
}
