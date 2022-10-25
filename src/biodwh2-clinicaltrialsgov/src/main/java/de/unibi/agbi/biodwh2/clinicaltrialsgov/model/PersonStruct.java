package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PersonStruct {
    @JsonProperty(value = "first_name")
    public String firstName;
    @JsonProperty(value = "middle_name")
    public String middleName;
    @JsonProperty(value = "last_name")
    public String lastName;
    @JsonProperty(value = "degrees")
    public String degrees;
}
