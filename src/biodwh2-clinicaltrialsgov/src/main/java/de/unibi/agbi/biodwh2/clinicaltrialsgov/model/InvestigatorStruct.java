package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InvestigatorStruct {
    @JsonProperty(value = "first_name")
    public String firstName;
    @JsonProperty(value = "middle_name")
    public String middleName;
    @JsonProperty(value = "last_name", required = true)
    public String lastName;
    public String degrees;
    public RoleEnum role;
    public String affiliation;
}
