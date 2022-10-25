package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactStruct extends PersonStruct {
    public String phone;
    @JsonProperty(value = "phone_ext")
    public String phoneExt;
    public String email;
}
