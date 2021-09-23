package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "agency_class", "lead_or_collaborator", "name"})
public class Sponsor {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("agency_class")
    public String agencyClass;
    @JsonProperty("lead_or_collaborator")
    public String leadOrCollaborator;
    @JsonProperty("name")
    public String name;
}
