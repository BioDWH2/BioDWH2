package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "organization", "name", "phone", "email"})
public class ResultContact {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("organization")
    public String organization;
    @JsonProperty("name")
    public String name;
    @JsonProperty("phone")
    public String phone;
    @JsonProperty("email")
    public String email;
}
