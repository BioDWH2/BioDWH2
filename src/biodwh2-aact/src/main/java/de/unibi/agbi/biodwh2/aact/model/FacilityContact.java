package de.unibi.agbi.biodwh2.aact.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "nct_id", "facility_id", "contact_type", "name", "email", "phone"})
public class FacilityContact {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("nct_id")
    public String nctId;
    @JsonProperty("facility_id")
    public Long facilityId;
    @JsonProperty("contact_type")
    public String contactType;
    @JsonProperty("name")
    public String name;
    @JsonProperty("email")
    public String email;
    @JsonProperty("phone")
    public String phone;
}
