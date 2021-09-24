package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "recruitment_details", "pre_assignment_details"})
public class ParticipantFlow {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("recruitment_details")
    public String recruitmentDetails;
    @JsonProperty("pre_assignment_details")
    public String preAssignmentDetails;
}
