package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "contact_type", "name", "phone", "email"})
public class CentralContact {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("contact_type")
    public String contactType;
    @JsonProperty("name")
    public String name;
    @JsonProperty("phone")
    public String phone;
    @JsonProperty("email")
    public String email;
}
