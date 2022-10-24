package de.unibi.agbi.biodwh2.clinicaltrialsgov.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PointOfContactStruct {
    @JsonProperty(value = "name_or_title", required = true)
    public String nameOrTitle;
    public String organization;
    public String phone;
    public String email;
}
