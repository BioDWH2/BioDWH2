package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactStruct {
    @JsonProperty(value = "first_name")
    public String firstName;
    @JsonProperty(value = "middle_name")
    public String middleName;
    @JsonProperty(value = "last_name")
    public String lastName;
    public String degrees;
    public String phone;
    @JsonProperty(value = "phone_ext")
    public String phoneExt;
    public String email;
}
