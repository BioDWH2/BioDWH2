package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "role", "name", "affiliation"})
public class OverallOfficial {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("role")
    public String role;
    @JsonProperty("name")
    public String name;
    @JsonProperty("affiliation")
    public String affiliation;
}
