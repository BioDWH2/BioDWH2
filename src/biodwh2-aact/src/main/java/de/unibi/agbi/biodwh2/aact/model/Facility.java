package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "status", "name", "city", "state", "zip", "country"})
public class Facility {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("status")
    public String status;
    @JsonProperty("name")
    public String name;
    @JsonProperty("city")
    public String city;
    @JsonProperty("state")
    public String state;
    @JsonProperty("zip")
    public String zip;
    @JsonProperty("country")
    public String country;
}
