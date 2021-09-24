package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "responsible_party_type", "name", "title", "organization", "affiliation"})
public class ResponsibleParty {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("responsible_party_type")
    public String responsiblePartyType;
    @JsonProperty("name")
    public String name;
    @JsonProperty("title")
    public String title;
    @JsonProperty("organization")
    public String organization;
    @JsonProperty("affiliation")
    public String affiliation;
}
